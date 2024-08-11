package edu.northeastern.group2final.overview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.suggestion.model.Suggestion;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {
    private List<Suggestion> suggestions;
    private SimpleDateFormat dateFormat;

    public SuggestionAdapter(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);
        holder.bind(suggestion);
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private TextView suggestionContent;
        private TextView suggestionDate;

        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionContent = itemView.findViewById(R.id.suggestionContent);
            suggestionDate = itemView.findViewById(R.id.suggestionDate);
        }

        void bind(Suggestion suggestion) {
            suggestionContent.setText(suggestion.getContent());
            suggestionDate.setText(dateFormat.format(suggestion.getCreatedAt()));
        }
    }
}
