inherit upx_compress

do_install_append() {
if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		rm ${D}/etc/init.d/vsftpd || true
fi
}
