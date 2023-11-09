package fpt.edu.nhom4.Data;

import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.DAO.BudgetDAO;
import fpt.edu.nhom4.Entity.BudgetEntity;
import fpt.edu.nhom4.R;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.transViewHolder>{

    List<BudgetEntity> cartList;
    BudgetEntity item;
    public TransAdapter(List<BudgetEntity> cartList) {
            this.cartList = cartList;
            }

    @NonNull
    @Override
    public transViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_item, parent, false);
            return new transViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull transViewHolder holder, int position) {
        item = cartList.get(position);
        if (item != null) {
            holder.Note.setText(item.getNote());
            double amount = item.getAmount();
            NumberFormat formatter = new DecimalFormat("#,###");
            holder.Amount.setText(formatter.format(amount) + " VND");
            holder.Amount.setTextColor(ContextCompat.getColor(holder.Amount.getContext(), item.getCategoryColor()));
            Date date;
            Calendar calendar = Calendar.getInstance();
            date = calendar.getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(date);
            holder.Date.setText(formattedDate);
        }
    }




    @Override
    public int getItemCount() {
            return cartList != null ? cartList.size() : 0;
            }

    public class transViewHolder extends RecyclerView.ViewHolder {
        TextView Note, Amount, Date; // Thêm các TextView hoặc View khác nếu cần
        ImageView icDelete;

        public transViewHolder(@NonNull View itemView) {
            super(itemView);
            Note = itemView.findViewById(R.id.textNote);
            Amount = itemView.findViewById(R.id.textAmount);
            Date = itemView.findViewById(R.id.txtDate);
            icDelete = itemView.findViewById(R.id.icDelete);

            icDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        int position = getAdapterPosition();
                        DBConnection dbConnection = DBConnection.getInitialDatabase(v.getContext());
                        BudgetDAO budgetDAO = dbConnection.budgetDAO();
                        BudgetEntity budget = cartList.get(position);
                        budget = budgetDAO.findByID(budget.getId());
                        budget.setInCart(false);
                        budgetDAO.updateBudget(budget);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            cartList.remove(position);
                            notifyItemRemoved(position);
                            // Update other UI elements or notify other components if necessary
                        });
                    });
                }
             });
        }
    }
}
