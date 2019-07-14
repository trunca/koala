DESCRIPTION = "User space libs for ${MACHINE}"
SECTION = "base"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "su980"

PV = "20140321"

SRC_URI = "https://raw.githubusercontent.com/OpenVisionE2/hypercube-files/master/${MACHINE}-user-libs-${PV}.tar.gz"

SRC_URI[md5sum] = "7dc15feab7fb25df67dc8e5289e369a6"
SRC_URI[sha256sum] = "46c70c043f0271ba0de6c32e51440e128ac46bb34b59acbf164b4205bf3a57ac"

S = "${WORKDIR}"

FILES_${PN} = "${libdir}/*"

do_install() {
	install -d ${D}/${libdir}
	for f in *.a *.so; do
		install -m 0644 ${S}/$f ${D}/${libdir}
	done
}

# The compiled binaries don't provide sonames.
SOLIBS = "${SOLIBSDEV}"

INSANE_SKIP_${PN} = "ldflags"

# do not put the *.so into -dev package
FILES_${PN}-dev = ""
