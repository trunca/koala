SUMMARY = "Linux kernel for ${MACHINE}"
SECTION = "kernel"
LICENSE = "GPLv2"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "su980"

inherit kernel machine_kernel_pr

LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI[kernel.md5sum] = "10eebcb0178fb4540e2165bfd7efc7ad"
SRC_URI[kernel.sha256sum] = "fa395fec7de633df1cb85b6248b8f35af98380ed128a8bc465fb48bc4d252633"

SRC_URI[boot.md5sum] = "a350020ed37513b51af9bea593457c9c"
SRC_URI[boot.sha256sum] = "afc09071adc3b5022f7e739e1bb0243923f757ca2e7d42f6ed0a226093232b93"

SRC_URI[loader.md5sum] = "c92ee99ad8e8732678d6bf090d17cbc2"
SRC_URI[loader.sha256sum] = "9d087534f449aa4eecdab2b049c15d934e53c1a7530540a714034404ded46f29"

SRC_URI[upgrade.md5sum] = "609d0e7bcc785d74ff6d90c71014fa16"
SRC_URI[upgrade.sha256sum] = "f52f35077da6c60564f9b564777a99ce7b79119c86c5b4affdcb931ad244d9bd"

# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG_kernel-base = "kernel-base"
PKG_kernel-image = "kernel-image"
RPROVIDES_kernel-base = "kernel-${KERNEL_VERSION}"
RPROVIDES_kernel-image = "kernel-image-${KERNEL_VERSION}"

DEPENDS += "virtual/${TARGET_PREFIX}gcc"

SRC_URI = "\
	${KERNELORG_MIRROR}/linux/kernel/v${PV}/linux-${PV}.tar.bz2;name=kernel \
	https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/boot.bin;name=boot \
	https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/loader.bin;name=loader \
	https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/upgrade.scr;name=upgrade \
	file://defconfig \
	file://100-arm-linux.patch \
	file://101-apollo_stb.patch \
	file://102-unionfs-2.5.4.patch \
	file://103-apollo_usb.patch \
	file://104-apollo_spi_callbackfix.patch \
	file://105-apollo_sata_fuse_fix.patch \
	file://106-kronos_stb.patch \
	file://107-apollo_linux_warning_fix.patch \
	file://108-apollo_spi_dmac_rf4cefix.patch \
	file://109-apollo_serialwrapperfix.patch \
	file://110-apollo_sfc_div_u64_fix.patch \
	file://111-apollo_mtd_define_fix.patch \
	file://112-apollo_usb_code_from_28kernel.patch \
	file://113-apollo_active_standby.patch \
	file://114-apollo_sfc32M.patch \
	file://115-apollo_sfc_jffs2_fix.patch \
	file://116-apollo_ip3106_kgdb.patch \
	file://117-apollo_sfc_jffs2_32M.patch \
	file://118-apollo_syscall.patch \
	file://119-apollo_perf_events.patch \
	file://120-apollo_cortexa9_errata.patch \
	file://121-apollo_bzImage_support.patch \
	file://122-apollo_cortexa9_freq_detect.patch \
	file://123-apollo_usb_ehci_handlers.patch \
	file://124-apollo_iic_greset_fix.patch \
	file://125-apollo-otg_redesign.patch \
	file://126-apollo_various_fixes.patch \
	file://128-apollo_gcc_4.5_support.patch \
	file://130-apollo_ethernet_AnDSP_changes.patch \
	file://132-apollo-mtd_devices.patch \
	file://133-apollo-numonyx_flash.patch \
	file://134-apollo-uart_isr.patch \
	file://135-apollo-spi_gp500.patch \
	file://136-apollo-gpio_apis.patch \
	file://137-apollo_chip_rev_detect.patch \
	file://139-apollo_usb_gadget_fshs.patch \
	file://141-apollo_usb_gadget_flag_cleanup.patch \
	file://144-nand_pagesize.patch \
	file://145-apollo_usb_gadget_plugfest_fixes.patch \
	file://146-apollo_usb_no_otg_usbcv_fix.patch \
	file://147-apollo_gmac0_rgmii.patch \
	file://148-apollo_usb_vid_pid_fix.patch \
	file://156-apollo_sdio_pci_support.patch \
	file://157-apollo_linux_dvb_extension.patch \
	file://158-apollo_usb_reset_fix.patch \
	file://sen5_input_repeat.patch \
	file://sen5_fix_network_tx_crash.patch \
	file://sen5_s25fl129p_spi_flash.patch \
	file://0001-ARM-6329-1-wire-up-sys_accept4-on-ARM.patch \
	file://kernel-add-support-for-gcc6.patch \
	file://fix_return_address_warning.patch \
	file://rtl8712-fix-warnings.patch \
	file://rtl8187se-fix-warnings.patch \
	file://timeconst_perl5.patch \
	file://0001-dvb_frontend-backport-multistream-support.patch \
	"

S = "${WORKDIR}/linux-${PV}"

inherit kernel

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_OUTPUT = "vmlinux"
KERNEL_IMAGETYPE = "vmlinux"
KERNEL_IMAGEDEST = "tmp"

FILES_kernel-image = "${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}*"

kernel_do_deploy_append() {
	cp -a ${WORKDIR}/boot.bin ${WORKDIR}/loader.bin ${WORKDIR}/upgrade.scr ${DEPLOYDIR}/
}
