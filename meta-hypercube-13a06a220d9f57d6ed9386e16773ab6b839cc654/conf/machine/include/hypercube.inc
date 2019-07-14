# We know about rtc
include conf/machine/include/build-extras.inc

BOX_BRAND = "hypercube"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "\
	${MACHINE}-dvb-modules \
	"

EXTRA_IMAGEDEPENDS += "\
	${MACHINE}-uboot-tools \
	${MACHINE}-user-libs \
	${MACHINE}-extra-modules \
	"

MACHINE_EXTRA_RDEPENDS += "\
	${MACHINE}-uboot-tools \
	${MACHINE}-user-libs \
	"

DVBMEDIASINK_CONFIG = "--with-dts --with-limited-mpeg4v2 --with-eac3 --with-wma --with-wmv --with-pcm"

IMAGE_FSTYPES ?= "jffs2 su980-usbimg"

IMAGE_CLASSES += "usb_image-su980"

JFFS2_ERASEBLOCK = "0x20000"
EXTRA_IMAGECMD_jffs2 = "--disable-compressor=lzo --compression-mode=size"

UPDATE_DIR ?= "e2"

# TODO: this needs fixing...
IMAGE_CMD_jffs2_append = "; \
	cd ${DEPLOY_DIR_IMAGE}; \
	rm -rf ${MACHINE}; \
	mkdir -p ${MACHINE}/${UPDATE_DIR}; \
	cp -a boot.bin loader.bin upgrade.scr ${MACHINE}/; \
	cp -L ${KERNEL_IMAGETYPE}-${MACHINE}.bin ${MACHINE}/${UPDATE_DIR}/e2-kernel.bin; \
	cp -a ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.jffs2 ${MACHINE}/${UPDATE_DIR}/e2-rootfs.jffs2; \
	zip -r ${IMAGE_NAME}_usb.zip ${MACHINE}/*; \
	rm -rf ${MACHINE}; \
	"

MACHINE_FEATURES += "alsa 32bpp hdtv usbhost wifi 3dtv ci wifiusblegacy legacykernel oldkernel"

DVBPROVIDER = "kernel"

include conf/machine/include/autoload-filesystems.inc
include conf/machine/include/autoload-usbserial.inc

KERNEL_IMAGETYPE ?= "Image"

SERIAL_CONSOLE = "115200 ttyS1"

PREFERRED_PROVIDER_virtual/kernel = "linux-su980"
PREFERRED_VERSION_linux-cube = "2.6.34"
PREFERRED_VERSION_samba = "3.%"
PREFERRED_VERSION_wpa-supplicant = "0.5.8"
PREFERRED_VERSION_rtl8192cu = "3.3.0"
PREFERRED_VERSION_wscan = "20130331"

OLDEST_KERNEL = "2.6.34"
GCCVERSION = "4.9.4"
GLIBCVERSION = "2.19"

GLIBC_EXTRA_OECONF = "--with-tls --with-__thread"

DEFAULTTUNE = "cortexa9"

require conf/machine/include/tune-cortexa9.inc

TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "cortexa9", "-march=armv7-a -mtune=cortex-a9 -mfpu=vfpv3-d16 -mfloat-abi=softfp", "", d)}"
