package com.king.cai.examination;

import com.king.cai.R;

import android.widget.ListView;


public class WaitingStatus extends PaperStatus{

	protected WaitingStatus(PaperActivity owner) {
		super(owner);
	}

	@Override
	public void onCommitClick() {
		mStatusOwner.showToast(R.string.Committing);		
	}

	@Override
	public void onFilterClick(ListView listView, PaperViewAdapter fullAdapter) {
		mStatusOwner.showToast(R.string.CommittingFilter);
	}

	@Override
	public void doGettingItemView(ItemViewHolder holder, Integer id,
			int fontsize) {
		holder.doGettingItemViews(id, fontsize);
	}

	@Override
	public void EnterStatus() {
		mStatusOwner.showToast(R.string.Committing);		
	}

	@Override
	public void LeaveStatus() {
	}
}

