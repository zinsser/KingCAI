package com.king.cai.examination;

import android.widget.ListView;


public class WaitingStatus extends PaperStatus{

	protected WaitingStatus(PaperActivity owner) {
		super(owner);
	}

	@Override
	public void onCommitClick() {
		mStatusOwner.switch2CommitStatus();		
	}

	@Override
	public void onFilterClick(ListView listView, PaperViewAdapter fullAdapter) {
	}

	@Override
	public void doGettingItemView(ItemViewHolder holder, Integer id,
			int fontsize) {
		holder.doGettingItemViews(id, fontsize);
	}

	@Override
	public void EnterStatus() {
	}

	@Override
	public void LeaveStatus() {
	}
}
