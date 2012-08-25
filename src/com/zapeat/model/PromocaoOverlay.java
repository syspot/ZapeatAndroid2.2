package com.zapeat.model;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.zapeat.activity.R;
import com.zapeat.util.Utilitario;

public class PromocaoOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<Promocao> promocoes;
	private Context context;

	public PromocaoOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		this.promocoes = new ArrayList<Promocao>();
		 populate();
	}

	public void addOverlay(Promocao overlay) {
		promocoes.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return promocoes.get(i);
	}

	@Override
	public int size() {

		return promocoes.size();
	}

	public PromocaoOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		this.promocoes = new ArrayList<Promocao>();
		 populate();
	}

	@Override
	protected boolean onTap(int index) {
		this.showDialogPromocao(promocoes.get(index));
		return true;
	}

	private void showDialogPromocao(Promocao promocao) {
		final Dialog dialog = new Dialog(this.context);
		dialog.setContentView(R.layout.dialog_promocao_ro);
		dialog.setTitle("Detalhes da promoção");

		TextView text = (TextView) dialog.findViewById(R.id.textDialogPromocao);
		text.setText(promocao.getLocalidade() + "\n\n" + promocao.getDescricao() + "\n de " + promocao.getPrecoOriginal() + " por " + promocao.getPrecoPromocional() + "\n\n Esta promoção encerra em " + promocao.getDataFinal() + " às " + promocao.getHoraFinal() + "\n\n");

		Button dialogButton = (Button) dialog.findViewById(R.id.btFecharDialogPromocao);

		ImageView image = (ImageView) dialog.findViewById(R.id.imageDialogPromocao);

		Bitmap bmp = Utilitario.getImage(promocao.getId());

		if (bmp == null) {

			image.setImageResource(R.drawable.ic_launcher);

		} else {
			image.setImageBitmap(bmp);
			image.setMaxHeight(130);
			image.setMinimumHeight(130);
			image.setMinimumWidth(130);
			image.setMaxWidth(130);
		}

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}
