PACKAGE_ARCH := "${MACHINE_ARCH}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://004_gdk_input_for_hbbtv.patch "

PACKAGECONFIG = "directfb"
