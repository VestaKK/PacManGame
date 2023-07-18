import matachi.mapeditor.editor.Controller;

public class Driver {
	public static void main(String[] args) {

		if (args.length == 1) {
			new Controller(args[0]);
		} else {
			new Controller();
		}
	}
}
