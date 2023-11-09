package fpt.edu.nhom4.Data;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.DAO.BudgetDAO;
import fpt.edu.nhom4.Entity.BudgetEntity;
import fpt.edu.nhom4.Entity.TransItem;
import fpt.edu.nhom4.R;
import fpt.edu.nhom4.TransactionActivity;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<BudgetEntity> listBudget;
    private RecyclerInterface iRecyclerView;
    private List<BudgetEntity> selectedItems = new ArrayList<>();
    private BudgetEntity budget;


    public DataAdapter(List<BudgetEntity> listBudget, RecyclerInterface listener) {
        this.listBudget = listBudget;
        this.iRecyclerView = listener;
    }



    public void setData(List<BudgetEntity> list) {
        this.listBudget = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        budget = listBudget.get(position);
        if (budget != null) {

            holder.Note.setText(budget.getNote());
            double db = budget.getAmount();
            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(db);
            holder.Amount.setText(formattedNumber + " VND");
            holder.Amount.setTextColor(ContextCompat.getColor(holder.Amount.getContext(), budget.getCategoryColor()));

            holder.StartDate.setText(budget.getStartDate());
            holder.EndDate.setText(budget.getEndDate());
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iRecyclerView.itemOnClick(budget);
                }
            });


    }
    }

    @Override
    public int getItemCount() {
        return listBudget != null ? listBudget.size() : 0;
    }



    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView Note;
        TextView Amount;
        TextView StartDate;
        TextView EndDate;
        LinearLayout layoutItem;
        ImageView icAdd, icDelete;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            icDelete = itemView.findViewById(R.id.icDelete);
            icDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        budget = listBudget.get(position);
                        int ID = budget.getId();


                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            DBConnection dbConnection = DBConnection.getInitialDatabase(v.getContext());
                            BudgetDAO budgetDAO = dbConnection.budgetDAO();
                            budgetDAO.deleteItemByIDs(ID);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                // Remove the deleted item from the list and notify the adapter
                                listBudget.remove(position);
                                notifyItemRemoved(position);
                            });
                        });
                        executor.shutdown();
                }
                }
            });
            icAdd = itemView.findViewById(R.id.icAdd);

            icAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            DBConnection dbConnection = DBConnection.getInitialDatabase(v.getContext());
                            BudgetDAO budgetDAO = dbConnection.budgetDAO();
                            BudgetEntity budget = listBudget.get(position);
                            budget = budgetDAO.findByID(budget.getId());
                            budget.setInCart(true);
                            budgetDAO.updateBudget(budget);
                            TransItem.getInstance().addItem(budget);
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        notifyItemChanged(position); // Ensure this runs on the main thread
                                    });
                        });

                        Intent intent = new Intent(v.getContext(), TransactionActivity.class);
                        v.getContext().startActivity(intent);

                    }
                }
            });
            Note = itemView.findViewById(R.id.textNote);
            Amount = itemView.findViewById(R.id.textAmount);
            StartDate = itemView.findViewById(R.id.textDate);
            EndDate = itemView.findViewById(R.id.textEndDate);
            layoutItem = itemView.findViewById(R.id.mainItemLayout);
             // Assuming you have a layout with this id for the entire item
        }
    }
}
