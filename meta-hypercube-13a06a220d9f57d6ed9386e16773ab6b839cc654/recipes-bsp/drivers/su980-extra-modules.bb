DESCRIPTION = "Hardware drivers for ${MACHINE}"
SECTION = "base"
PRIORITY = "required"
LICENSE = "CLOSED"
PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "su980"

KV = "2.6.34"
PV = "20131126"

S = "${WORKDIR}"

SRC_URI = "https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/${MACHINE}-extra-modules-${PV}.tar.gz"

SRC_URI[md5sum] = "a4c34f141630f96353a3b884679e4709"
SRC_URI[sha256sum] = "8bf7cf02dbd8fa516667ad924fbd1e52643a8fe90ba2af87fe2a2ba0ef1b6b17"

FILES_${PN} += " \
	${base_libdir}/* \
	${sysconfdir}/* \
	"

do_compile() {
}

do_install() {
	install -d ${D}/lib/modules/${KV}/extra/
	for f in *.ko; do
		install -m 0644 ${WORKDIR}/$f ${D}/lib/modules/${KV}/extra/$f;
	done

	install -d ${D}/${sysconfdir}/modules-load.d
	for i in `ls | grep \\.ko | sed -e 's/.ko//g'`; do
		echo $i >> ${D}/${sysconfdir}/modules-load.d/extra.conf
	done
}
