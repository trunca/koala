inherit image_types

## this is heavily inspired by the raspberry pi sdimage class.
## ...and more or less copied over form meta-coolstream
# Create an image that can by written onto an USB stick.
#
# The disk layout used is:
#
#    0                      -> IMAGE_ROOTFS_ALIGNMENT - reserved for other data
#    IMAGE_ROOTFS_ALIGNMENT -> BOOT_SPACE             - u-boot script and kernel
#    BOOT_SPACE             -> USBIMG_SIZE             - rootfs
#
#                                                     Default Free space = 1.3x
#                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                     <--------->
#            1MiB              20MiB           USBIMG_ROOTFS
# <-----------------------> <----------> <---------------------->
#  ------------------------ ------------ ------------------------
# | IMAGE_ROOTFS_ALIGNMENT | BOOT_SPACE | ROOTFS_SIZE            |
#  ------------------------ ------------ ------------------------
# ^                        ^            ^                        ^
# |                        |            |                        |
# 0                      1MiB     1MiB + 20MiB       1MiB + 20Mib + USBIMG_ROOTFS

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "KERNEL"

# Boot partition size [in KiB] (will be rounded up to IMAGE_ROOTFS_ALIGNMENT)
BOOT_SPACE ?= "20480"

# Set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT = "1024"

# Use an uncompressed ext3 by default as rootfs
USBIMG_ROOTFS_TYPE ?= "ext3"
USBIMG_ROOTFS = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.${USBIMG_ROOTFS_TYPE}"

IMAGE_DEPENDS_su980-usbimg = " \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    virtual/kernel:do_populate_sysroot \
    e2fsprogs-native:do_populate_sysroot \
"

# USB image name
USBIMG = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.su980-usbimg.img"

# Compression method to apply to USBIMG after it has been created. Supported
# compression formats are "gzip", "bzip2" or "xz". The original .*-usbimg file
# is kept and a new compressed file is created if one of these compression
# formats is chosen. If USBIMG_COMPRESSION is set to any other value it is
# silently ignored.
#USBIMG_COMPRESSION ?= ""

# Additional files and/or directories to be copied into the vfat partition from the IMAGE_ROOTFS.
FATPAYLOAD ?= ""

IMAGEDATESTAMP = "${@time.strftime('%Y.%m.%d',time.gmtime())}"

IMAGE_CMD_su980-usbimg () {
	# Cleanup old stuff
	rm -f "${USBIMG_ROOTFS}"
	rm -f "${USBIMG}"
	rm -f ${WORKDIR}/boot.img

	# Create ext-fs image
	dd if=/dev/zero of="${USBIMG_ROOTFS}" seek=${ROOTFS_SIZE} count=0 bs=1024
	mkfs.${USBIMG_ROOTFS_TYPE} -F -i 4096 "${USBIMG_ROOTFS}" -d "${IMAGE_ROOTFS}"

	# Align partitions
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	ROOTFS_SIZE=`du -bks ${USBIMG_ROOTFS} | awk '{print $1}'`
        # Round up RootFS size to the alignment size as well
	ROOTFS_SIZE_ALIGNED=$(expr ${ROOTFS_SIZE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	ROOTFS_SIZE_ALIGNED=$(expr ${ROOTFS_SIZE_ALIGNED} - ${ROOTFS_SIZE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	USBIMG_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + ${ROOTFS_SIZE_ALIGNED})

	echo "Creating filesystem with Boot partition ${BOOT_SPACE_ALIGNED} KiB and RootFS ${ROOTFS_SIZE_ALIGNED} KiB"

	# Initialize usbstick image file
	dd if=/dev/zero of=${USBIMG} bs=1024 count=0 seek=${USBIMG_SIZE}

	# Create partition table
	parted -s ${USBIMG} mklabel msdos
	# Create boot partition and mark it as bootable (not necessary, but does not hurt)
	parted -s ${USBIMG} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT})
	parted -s ${USBIMG} set 1 boot on
	# Create rootfs partition to the end of disk
	parted -s ${USBIMG} -- unit KiB mkpart primary ext2 $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT}) -1s
	parted ${USBIMG} print

	# Create a vfat image with boot files
	BOOT_BLOCKS=$(LC_ALL=C parted -s ${USBIMG} unit b print | awk '/ 1 / { print substr($4, 1, length($4 -1)) / 512 /2 }')
	mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 -C ${WORKDIR}/boot.img $BOOT_BLOCKS
	cp -f ${WORKDIR}/boot.img ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin

	if [ -n ${FATPAYLOAD} ] ; then
		echo "Copying payload into VFAT"
		for entry in ${FATPAYLOAD} ; do
			# add the || true to stop aborting on vfat issues like not supporting .~lock files
			mcopy -i ${WORKDIR}/boot.img -s -v ${IMAGE_ROOTFS}$entry :: || true
		done
	fi

	# Add stamp file
	echo "${IMAGE_NAME}-${IMAGEDATESTAMP}" > ${WORKDIR}/image-version-info
	mcopy -i ${WORKDIR}/boot.img -v ${WORKDIR}//image-version-info ::

	# Burn Partitions
	dd if=${WORKDIR}/boot.img of=${USBIMG} conv=notrunc seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	# If USBIMG_ROOTFS_TYPE is a .xz file use xzcat
	if echo "${USBIMG_ROOTFS_TYPE}" | egrep -q "*\.xz"
	then
		xzcat ${USBIMG_ROOTFS} | dd of=${USBIMG} conv=notrunc seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	else
		dd if=${USBIMG_ROOTFS} of=${USBIMG} conv=notrunc seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	fi

	parted ${USBIMG} print

	# ensure that the image is a sparse file
	cp -a --sparse=always "${USBIMG}" "${USBIMG}".sparse
	mv "${USBIMG}".sparse "${USBIMG}"
	# Compress and cleanup
	zip -j -9 "${USBIMG}".zip "${USBIMG}"
	rm -f "${USBIMG}"
	rm -f "${USBIMG_ROOTFS}"
}

# ROOTFS_POSTPROCESS_COMMAND += ""
