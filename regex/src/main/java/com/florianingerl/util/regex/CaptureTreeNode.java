package com.florianingerl.util.regex;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CaptureTreeNode {
	int groupNumber;
	String groupName;
	Capture capture;
	List<CaptureTreeNode> children = new LinkedList<CaptureTreeNode>();
	CaptureTreeNode parent;
	boolean recursion;

	public int getGroupNumber() {
		return groupNumber;
	}

	public String getGroupName() {
		return groupName;
	}

	public Capture getCapture() {
		return capture;
	}

	public List<CaptureTreeNode> getChildren() {
		return children;
	}

	public CaptureTreeNode getParent() {
		return parent;
	}

	public boolean isRecursion() {
		return recursion;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, 0);
		return sb.toString();
	}

	void setGroupName(Map<Integer, String> groupNames) {
		groupName = groupNames.get(groupNumber);
		for (CaptureTreeNode ctn : children)
			ctn.setGroupName(groupNames);
	}

	private void toString(StringBuilder sb, int numTabs) {

		for (int i = 0; i < numTabs; ++i) {
			sb.append("\t");
		}
		sb.append(groupName != null ? groupName : groupNumber);
		sb.append("\n");

		for (CaptureTreeNode ctn : children) {
			ctn.toString(sb, numTabs + 1);
		}

	}

	Capture findGroup(int group) {
		return findGroup(group, true);
	}

	private Capture findGroup(int group, boolean searchParent) {

		ListIterator<CaptureTreeNode> it = children.listIterator(children.size());
		Capture c = findGroup(group, it);
		if (c != null)
			return c;

		if (searchParent) {
			CaptureTreeNode current = this;
			while (!current.recursion && current.parent != null) {
				current = current.parent;
				it = current.children.listIterator(current.children.size() - 1);
				c = findGroup(group, it);
				if (c != null)
					return c;
			}
		}

		return null;
	}

	private Capture findGroup(int group, ListIterator<CaptureTreeNode> it) {
		while (it.hasPrevious()) {
			CaptureTreeNode child = it.previous();
			if (child.groupNumber == group)
				return child.capture;
			if (child.recursion)
				continue;
			Capture c = child.findGroup(group, false);
			if (c != null)
				return c;
		}
		return null;
	}

}