package org.example.classes;

import org.example.Identity;

@Identity(field = "identity")
public class IdentityCycleClass {
	public int value;
	public IdentityCycleClass cycle;
}
