package fpt.edu.nhom4.Data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpt.edu.nhom4.Entity.TransactionEntity;
import fpt.edu.nhom4.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.historyViewHolder>{

    List<TransactionEntity> cartList;
    TransactionEntity item;
    public HistoryAdapter(List<TransactionEntity> cartList) {
        this.cartList = cartList;
    }



    @NonNull
    @Override
    public historyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new historyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyViewHolder holder, int position) {
        item = cartList.get(position);
        if (item != null) {
            holder.note.setText(item.getNote());
            double amount = item.getAmount();
            NumberFormat formatter = new DecimalFormat("#,###");
            holder.Amount.setText(formatter.format(amount) + " VND");
            holder.Amount.setTextColor(ContextCompat.getColor(holder.Amount.getContext(), item.getCategoryColor()));
            holder.Date.setText(item.getTransDate());

        }
    }


    public void clear() {
        cartList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<TransactionEntity> newItems) {
        cartList.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public class historyViewHolder extends RecyclerView.ViewHolder {
        TextView note, Amount, Date; // Thêm các TextView hoặc View khác nếu cần
        ImageView icDelete;

        public historyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.textNote);
            Amount = itemView.findViewById(R.id.textAmount);
            Date = itemView.findViewById(R.id.txtDate);
        }
}
}
