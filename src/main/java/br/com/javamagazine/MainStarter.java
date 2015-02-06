package br.com.javamagazine;

import org.jboss.weld.environment.se.StartMain;

public class MainStarter extends StartMain {

	public MainStarter(String[] commandLineArgs) {
		super(commandLineArgs);
	}

	public static void main(String[] args) {
		new MainStarter(args).go();
	}
}
