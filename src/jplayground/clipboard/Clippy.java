package jplayground.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public final class Clippy implements ClipboardOwner {
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
	
	public Boolean hasOwnership() {
		return false;
	}
	
}
