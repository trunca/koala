SUMMARY = "Firmware files for bcm4339a0/ap6330"
LICENSE = "CLOSED"
require conf/license/license-close.inc

PACKAGE_ARCH = "all"

SRC_URI = "file://firmware-ap6330.zip"

S = "${WORKDIR}"

PACKAGES = "${PN}"
FILES_${PN} += "${base_libdir}/firmware/brcm"
FILES_${PN} += "${sysconfdir}/wifi/"

do_install() {
    install -d ${D}${sysconfdir}/wifi/
    install -m 0755 config.txt  ${D}${sysconfdir}/wifi/config.txt
    install -d ${D}${base_libdir}/firmware/brcm
    install -m 0644 ap6335e_nvram.txt ${D}${base_libdir}/firmware/brcm/ap6335e_nvram.txt
    install -m 0644 fw_bcm4339a0_ag.bin ${D}${base_libdir}/firmware/brcm/fw_bcm4339a0_ag.bin
}
