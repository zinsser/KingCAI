package com.king.cai;

public abstract class DBModel {
	public abstract int GetItemCount();
	public abstract String GetTag(int pos);
	public abstract String GetTableName();
	public abstract String GetTableCreatorString();
}
