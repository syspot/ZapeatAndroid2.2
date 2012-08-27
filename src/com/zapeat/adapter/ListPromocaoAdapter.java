package com.zapeat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zapeat.activity.R;
import com.zapeat.model.Promocao;
import com.zapeat.util.Utilitario;

public class ListPromocaoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Promocao> promocoes;

	public ListPromocaoAdapter(Context context, List<Promocao> promocoes) {
		this.promocoes = promocoes;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return promocoes.size();
	}

	@Override
	public Object getItem(int position) {
		return promocoes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<Promocao> getPromocoes() {
		return promocoes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Promocao item = promocoes.get(position);

		convertView = mInflater.inflate(R.layout.item_list, null);

		ImageView image = (ImageView) convertView.findViewById(R.id.list_image);

		Bitmap bmp = Utilitario.getImage(item.getId());

		if (bmp != null) {
			image.setImageBitmap(bmp);
		}

		((TextView) convertView.findViewById(R.id.titulo_promocao)).setText(item.getLocalidade());
		((TextView) convertView.findViewById(R.id.descricao_promocao)).setText(item.getDescricao());

		return convertView;
	}

}
