/*
  This file is part of Subsonic.
	Subsonic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	Subsonic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with Subsonic. If not, see <http://www.gnu.org/licenses/>.
	Copyright 2014 (C) Scott Jackson
*/

package github.daneren2005.dsub.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.ServerInfo;
import github.daneren2005.dsub.domain.User;
import github.daneren2005.dsub.util.ImageLoader;
import github.daneren2005.dsub.util.UserUtil;
import github.daneren2005.dsub.view.RecyclingImageView;
import github.daneren2005.dsub.view.SettingView;
import github.daneren2005.dsub.view.UpdateView;

import static github.daneren2005.dsub.domain.User.Setting;

public class SettingsAdapter extends SectionAdapter<Setting> {
	private static final String TAG = SettingsAdapter.class.getSimpleName();
	public final int VIEW_TYPE_SETTING = 1;

	private final User user;
	private final boolean editable;
	private final ImageLoader imageLoader;

	public SettingsAdapter(Context context, User user, List<String> headers, List<List<User.Setting>> settingSections, ImageLoader imageLoader, boolean editable, OnItemClickedListener<Setting> onItemClickedListener) {
		super(context, headers, settingSections, imageLoader != null);
		this.user = user;
		this.imageLoader = imageLoader;
		this.editable = editable;
		this.onItemClickedListener = onItemClickedListener;

		List<Setting> settings = sections.get(0);
		for(Setting setting: settings) {
			if(setting.getValue()) {
				addSelected(setting);
			}
		}
	}

	public UpdateView.UpdateViewHolder onCreateHeaderHolder(ViewGroup parent) {
		View header = LayoutInflater.from(context).inflate(R.layout.user_header, parent, false);
		return new UpdateView.UpdateViewHolder(header, false);
	}
	public void onBindHeaderHolder(UpdateView.UpdateViewHolder holder, String description) {
		if(description == null) {
			View header = holder.getView();

			RecyclingImageView coverArtView = (RecyclingImageView) header.findViewById(R.id.user_avatar);
			imageLoader.loadAvatar(context, coverArtView, user.getUsername());
			coverArtView.setOnInvalidated(new RecyclingImageView.OnInvalidated() {
				@Override
				public void onInvalidated(RecyclingImageView imageView) {
					imageLoader.loadAvatar(context, imageView, user.getUsername());
				}
			});

			TextView usernameView = (TextView) header.findViewById(R.id.user_username);
			usernameView.setText(user.getUsername());

			final TextView emailView = (TextView) header.findViewById(R.id.user_email);
			if (user.getEmail() != null) {
				emailView.setText(user.getEmail());
			} else {
				emailView.setVisibility(View.GONE);
			}
		} else {

		}
	}

	@Override
	public UpdateView.UpdateViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
		return new UpdateView.UpdateViewHolder(new SettingView(context));
	}

	@Override
	public void onBindViewHolder(UpdateView.UpdateViewHolder holder, Setting item, int viewType) {
		holder.getUpdateView().setObject(item, editable);
	}

	@Override
	public int getItemViewType(Setting item) {
		return VIEW_TYPE_SETTING;
	}

	@Override
	public void setChecked(UpdateView updateView, boolean checked) {
		if(updateView instanceof SettingView) {
			updateView.setChecked(checked);
		}
	}

	public static SettingsAdapter getSettingsAdapter(Context context, User user, ImageLoader imageLoader, OnItemClickedListener<Setting> onItemClickedListener) {
		List<String> headers = new ArrayList<>();
		List<List<User.Setting>> settingsSections = new ArrayList<>();
		settingsSections.add(user.getSettings());

		if(user.getMusicFolderSettings() != null) {
			headers.add(context.getResources().getString(R.string.admin_permissions));
			headers.add(context.getResources().getString(R.string.admin_musicFolders));
			settingsSections.add(user.getMusicFolderSettings());
		}
		return new SettingsAdapter(context, user, headers, settingsSections, imageLoader, UserUtil.isCurrentAdmin() && ServerInfo.checkServerVersion(context, "1.10"), onItemClickedListener);
	}
}
