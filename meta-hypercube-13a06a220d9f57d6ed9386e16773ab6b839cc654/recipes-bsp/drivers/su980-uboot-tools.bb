DESCRIPTION = "uboot tools for ${MACHINE}"
SECTION = "base"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "su980"

PV = "20181006"

SRC_URI = "https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/${MACHINE}-uboot-tools-${PV}.zip"

FILES_${PN} = "${bindir}/*"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/../getenv ${D}/${bindir}
	install -m 0755 ${S}/../setenv ${D}/${bindir}
}

SRC_URI[md5sum] = "6480e125421e20484e6b6a480b9cf62f"
SRC_URI[sha256sum] = "d980c8d7f90b8506fb6f1520ea7f73c469f81b079de393afad6d3bde76e12886"
