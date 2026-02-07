package service;

import java.time.LocalDate;
import java.util.ArrayList;

import model.Version;
import controller.SearchController.SnippetItem;
import util.DateTimeUtil;

public class FilterandSortService {

     //Filtering snippets based on language, tag, and date ranges.
     //Date range logic:
     //If only start date provided - from start date to today
     //If only end date provided - from last 1 month ago to end date
     //If neither - last 1 month (last 30 days from today)
     //If both - between start and end

    public static ArrayList<Version> applyFilters(
            ArrayList<Version> snippets,
            String language,
            String tag,
            LocalDate createdStart,
            LocalDate createdEnd,
            LocalDate modifiedStart,
            LocalDate modifiedEnd
    ) {
        ArrayList<Version> filteredList = new ArrayList<>();
        
        //apply default "last 1 month" logic for date ranges
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);
        
        //created date defaults
        LocalDate effectiveCreatedStart = createdStart;
        LocalDate effectiveCreatedEnd = createdEnd;
        if (createdStart == null && createdEnd == null) {
            effectiveCreatedStart = oneMonthAgo;
            effectiveCreatedEnd = now;
        } else if (createdStart == null && createdEnd != null) {
            effectiveCreatedStart = oneMonthAgo;
        } else if (createdStart != null && createdEnd == null) {
            effectiveCreatedEnd = now;
        }
        
        //modified date defaults
        LocalDate ModifiedStart = modifiedStart;
        LocalDate ModifiedEnd = modifiedEnd;
        if (modifiedStart == null && modifiedEnd == null) {
            ModifiedStart = oneMonthAgo;
            ModifiedEnd = now;
        } else if (modifiedStart == null && modifiedEnd != null) {
            ModifiedStart = oneMonthAgo;
        } else if (modifiedStart != null && modifiedEnd == null) {
            ModifiedEnd = now;
        }

        for (Version s : snippets) {
            //filter by language
            if (language != null && !language.isBlank() && !language.equals("All")) {
                if (s.getLanguage() == null || !s.getLanguage().equalsIgnoreCase(language)) {
                    continue;
                }
            }

            //filter by tag
            if (tag != null && !tag.isBlank() && !tag.equals("All")) {
                if (s.getTags() == null || !s.getTags().toLowerCase().contains(tag.toLowerCase())) {
                    continue;
                }
            }

            //filter by created date range (uses first version's timestamp)
            if (s.getTimestamp() != null) {
                try {
                    LocalDate createdDate = DateTimeUtil.parseTimestamp(s.getTimestamp());
                    if (effectiveCreatedStart != null && createdDate.isBefore(effectiveCreatedStart)) continue;
                    if (effectiveCreatedEnd != null && createdDate.isAfter(effectiveCreatedEnd)) continue;
                } catch (Exception e) {
                    //skip if date parsing doesn't work
                }
            }
            
            //filter by modified date range (uses current version's timestamp)
            if (s.getTimestamp() != null) {
                try {
                    LocalDate modifiedDate = DateTimeUtil.parseTimestamp(s.getTimestamp());
                    if (ModifiedStart != null && modifiedDate.isBefore(ModifiedStart)) continue;
                    if (ModifiedEnd != null && modifiedDate.isAfter(ModifiedEnd)) continue;
                } catch (Exception e) {
                    //skip if parsing date fails
                }
            }

            filteredList.add(s);
        }

        return filteredList;
    }

    public static ArrayList<Version> applySorting(ArrayList<Version> snippets, String sortField, boolean isAscending) {
        ArrayList<Version> sortedList = new ArrayList<>(snippets);
        
        //bubble sort algorithm for sorting
        for (int i = 0; i < sortedList.size() - 1; i++) {
            for (int j = 0; j < sortedList.size() - i - 1; j++) {
                
                Version a = sortedList.get(j);
                Version b = sortedList.get(j + 1);
                boolean swap = false;

                if (sortField.equalsIgnoreCase("title")) {
                    swap = a.getTitle().compareToIgnoreCase(b.getTitle()) > 0;
                } 
                else if (sortField.equalsIgnoreCase("language")) {
                    swap = a.getLanguage().compareToIgnoreCase(b.getLanguage()) > 0;
                }
                //descending order
                if (!isAscending) { 
                    swap = !swap; 
                }

                if (swap) {
                    sortedList.set(j, b);
                    sortedList.set(j + 1, a);
                }
            }
        }
        return sortedList;
    }

    //to limit results to 25 (for the Version and SnippetItem ArrayLists)
    public static ArrayList<Version> limitVersionResults(ArrayList<Version> items) {
        if (items.size() > 25) {
            return new ArrayList<>(items.subList(0, 25));
        }
        return items;
    }
    public static ArrayList<SnippetItem> limitSnippetItemResults(ArrayList<SnippetItem> items) {
        if (items.size() > 25) {
            return new ArrayList<>(items.subList(0, 25));
        }
        return items;
    }
}