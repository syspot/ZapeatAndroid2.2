package com.zapeat.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zapeat.activity.R;
import com.zapeat.model.Categoria;

public class ListPromocaoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Categoria> categorias;

	public ListPromocaoAdapter(Context context, List<Categoria> categorias) {
		this.categorias = categorias;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return categorias.size();
	}

	@Override
	public Object getItem(int position) {
		return categorias.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Categoria item = categorias.get(position);

		convertView = mInflater.inflate(R.layout.item_list, null);

		((TextView) convertView.findViewById(R.item_list.titulo)).setText(item.getDescricao());
		((TextView) convertView.findViewById(R.item_list.quantidade)).setText(item.getQuantidade().toString());

		return convertView;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

}
