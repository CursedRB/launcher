package pw.geckonerd.CursedRB.Launcher;

public enum EnumOS {
	WINDOWS,
	MACOS,
	OTHER;
	
	public static EnumOS getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("mac"))
			return MACOS;
		if(os.startsWith("win"))
			return WINDOWS;
		return OTHER;
	}
	
	public String toString() {
		if(this.equals(WINDOWS)) {
			return "win";
		} else if(this.equals(OTHER)) {
			return "linux";
		} else {
			return "macos";
		}
	}
}
