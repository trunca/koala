DESCRIPTION = "Hardware drivers for ${MACHINE}"
SECTION = "base"
PRIORITY = "required"
LICENSE = "CLOSED"
PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "su980"

KV = "2.6.34"
PV = "20131127"

S = "${WORKDIR}"

inherit module update-rc.d
KERNEL_MODULES_META_PACKAGE = "None"

SRC_URI = "https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/${MACHINE}-dvb-modules-${PV}.tar.gz"

SRC_URI[md5sum] = "a2f9c1870a17c62f4eea6f3f4ff5ba1d"
SRC_URI[sha256sum] = "5404e35f21a232e3db691933bd833a0362272ae801087dab78cc81c24b51ba95"

FILES_${PN} += " \
	${base_libdir}/* \
	${sysconfdir}/* \
	"

INITSCRIPT_NAME = "populate-private-nodes.sh"
INITSCRIPT_PARAMS = "start 20 S ."

do_compile() {
}

do_install() {
	install -d ${D}/lib/modules/${KV}/extra/
	for f in *.ko; do
		install -m 0644 ${WORKDIR}/$f ${D}/lib/modules/${KV}/extra/$f;
	done

	install -d ${D}/${base_libdir}/firmware
	install -m 644 ${WORKDIR}/firmware/* ${D}/${base_libdir}/firmware/

	install -d ${D}/${sysconfdir}/Wireless/RT2870STA
	install -m 644 ${WORKDIR}/RT2870STA.dat ${D}/${sysconfdir}/Wireless/RT2870STA

	install -d ${D}/${sysconfdir}/modules-load.d
	# most other modules depend on the lnx* drivers so sort them first
	for i in `ls *.ko | sed -e 's/.ko//g'`; do
		case $i in
			lnx*)	echo $i >> ${D}/${sysconfdir}/modules-load.d/_${MACHINE}.conf ;;
			*) ;;
		esac
	done
	for i in `ls *.ko | sed -e 's/.ko//g'`; do
		case $i in
			lnx*) ;;
			*)	echo $i >> ${D}/${sysconfdir}/modules-load.d/_${MACHINE}.conf ;;
		esac
	done

	install -d ${D}/${sysconfdir}/init.d
	install -m 755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${sysconfdir}/init.d/

	install -d ${D}/${sysconfdir}/modprobe.d
	install -m 644 ${WORKDIR}/vpmfbDrv.conf ${D}/${sysconfdir}/modprobe.d
}
