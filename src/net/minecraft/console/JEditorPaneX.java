package net.minecraft.console;

import java.awt.Color;

import javax.swing.JEditorPane;

public class JEditorPaneX extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public String getTXT() {
		int ss = getSelectionStart();
		int se = getSelectionStart();
		setSelectionStart(0);
		setSelectionEnd(getText().length());
		String s2 = getSelectedText();
		s2 = s2.replace("�","\n");
		setSelectionStart(ss);
		setSelectionEnd(se);
		return s2;
	}
	
	public String getHTML(String s) {
		String s2 = s;
		String ignore = "";
		//String style = "<style type=\"text/css\">body{ font-family: sans-serif; color: #000000; }</style>";
		s2 = s2.replaceAll("<html>", ignore);
		s2 = s2.replaceAll("</html>", ignore);
		s2 = s2.replaceAll("<body>", ignore);
		s2 = s2.replaceAll("</body>", ignore);
		s2 = s2.replaceAll("<head>", ignore);
		s2 = s2.replaceAll("</head>", ignore);
		s2 = s2.replaceAll("<p style=\"margin-top: 0\">",ignore);
		s2 = s2.replaceAll("</p>",ignore);
		while (s2.trim().replaceAll("<font face=\"sans-serif\">", "").trim().startsWith("�")) {
			s2 = s2.replace("�",ignore);
		}
		boolean doflush = (s2.trim().replaceAll("<font face=\"sans-serif\">", "").trim().startsWith("&#160;<br>")||s2.trim().replaceAll("<font face=\"sans-serif\">", "").trim().startsWith("<br>"));
		while (s2.trim().replaceAll("<font face=\"sans-serif\">", "").trim().startsWith("&#160;<br>")) {
			s2 = s2.replaceFirst("&#160;<br>",ignore);
		}
		while (s2.trim().replaceAll("<font face=\"sans-serif\">", "").trim().startsWith("<br>")) {
			s2 = s2.replace("<br>",ignore);
		}
		if (doflush) {
			System.out.print("");
		}
		s2 = s2.replaceAll("<none>", "<font>");
		s2 = s2.replaceAll("\n","�<br>");
		s2 = s2.replaceAll("</none>", "</font>");
		s2 = s2.replaceAll("<red>", "</font><font color=\"#ff0000\" style=\"font-family:sans-serif\">");
		s2 = s2.replaceAll("</red>", "</font><font style=\"font-family:sans-serif\">");
		String s3 = "<html><head></head><body><font style=\"font-family:sans-serif\">"+s2+"</font></body></html>";
		return s3;
	}
	
	public void append(String s) {
		setText(getHTML(getText()+s));
		setCaretPosition(getCaretPosition()+s.length());
	}
	
	public void append(String s, Color c) {
		//String br1 = "<none>";
		//String br2 = "</none>";
		if (c==Color.red) setText(getHTML(getText()+"<red>"+s+"</red>"));
		setCaretPosition(getCaretPosition()+s.length());
	}
	
	

}
