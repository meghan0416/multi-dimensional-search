/** Starter code for P3
 *  @author Meghan Grayson
 */

// Change to your net id
package packagename;

import java.util.List;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.HashMap;


// If you want to create additional classes, place them in this file as subclasses of MDS

public class MDS {

   /* Subclass for items */
   static class Item implements Comparable<Item> {
      
      Integer price, id;
      List<Integer> description;

      /* Constructor */
      public Item(Integer i, Integer p, List<Integer> d) {
         this.id = i;
         this.price = p;
         this.description = new LinkedList<>(d);
      }

      /* Make items comparable by price, with id as a tiebreaker */
      @Override
      public int compareTo(Item otherItem) {
         if(this.price.compareTo(otherItem.price) != 0) {
            return this.price.compareTo(otherItem.price);
         }
         else {
            return this.id.compareTo(otherItem.id);
         }
      }

   }

   // Add fields of MDS here
   HashMap<Integer, Item> idTable; // Hashtable with key of id
   // Hashtable with key of descriptor, containing trees of items (comparable by price and id)
   HashMap<Integer, TreeSet<Item>> descTable;
   int size; // How many items are in the store

    // Constructors
    /*
     * Default constructor
     */
   public MDS() {
      idTable = new HashMap<>();
      descTable = new HashMap<>();
      size = 0;
   }


    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */
    public int insert(int id, int price, java.util.List<Integer> list) {
      // If item ID not yet in the store
      if(idTable.get(id) == null) {
         // Create the new item
         Item i = new Item(id, price, list);

         // Add to the hashtable by id
         idTable.put(id, i);

         // Add to the hashtable by description
         for(Integer e : i.description) {
            TreeSet<Item> t = descTable.get(e);

            // Create tree at e if not yet added
            if(t == null) {
               t = new TreeSet<>();
            }
            // Add item to tree at e
            t.add(i);
            descTable.put(e, t);
         }
         // Increment size
         size++;
         return 1;
      }

      // Otherwise need to update existing entries in both tables
      else {
         Item j = idTable.get(id);
         Item i;

         // If list is null, only update the price in the descriptions table
         if(list == null || list.size() == 0) {
            // Define new item with new price
            i = new Item(id, price, j.description);
            // Replace in id hashtable
            idTable.put(id, i);
            // Remove old item and add new item in tree at description
            for(Integer e : i.description) {
               TreeSet<Item> t = descTable.get(e);
               if(t != null) {
                  t.remove(j); // Remove the old item here
                  t.add(i); // Add the new item
               }
            }
         }
         // Otherwise, need to update price and description
         else {
            // Define new item with new price and list
            i = new Item(id, price, list);
            // Replace in id hashtable
            idTable.put(id, i);
            // Remove from old spot in desc table
            for(Integer e : j.description) {
               TreeSet<Item> t = descTable.get(e);
               if(t != null) {
                  t.remove(j);
                  if(t.size() == 0) {
                     descTable.remove(e); // If tree at description int e is empty now, remove tree
                  }
               }
            }
            // Add to new spots in table
            for(Integer e : list) {
               TreeSet<Item> t = descTable.get(e);

               // Create tree at new descr if needed
               if(t == null) {
                  t = new TreeSet<>();
               }
               // Add to the tree
               t.add(i);
               descTable.put(e, t);
            }
         }
         return 0;
      }
   }

   // b. Find(id): return price of item with given id (or 0, if not found).
   public int find(int id) {
      // Can use just the id hashtable
      Item i = idTable.get(id);
      if(i == null) return 0;
      return i.price;
   }

   /* 
      c. Delete(id): delete item from storage.  Returns the sum of the
      ints that are in the description of the item deleted,
      or 0, if such an id did not exist.
   */
   public int delete(int id) {
      Item i = idTable.get(id);
      // If item not here, return 0
      if(i == null) { return 0; }

      // Remove from the id hashtable
      idTable.remove(id);

      // Remove from the description hashtable
      int sum = 0;

      for(Integer e : i.description) {
         TreeSet<Item> t = descTable.get(e);
         sum += e; // Sum the description ints
         if(t != null) {
            t.remove(i); // Remove item from this hashtable
            if(t.size() == 0) { // If no more tree nodes at this description, remove from description table
               descTable.remove(e);
            }
         }
      }
      size--; // Decrement size
      return sum;
   }

   /* 
      d. FindMinPrice(n): given an integer, find items whose description
      contains that number (exact match with one of the ints in the
      item's description), and return lowest price of those items.
      Return 0 if there is no such item.
   */
   public int findMinPrice(int n) {
      // Check the tree at description n
      TreeSet<Item> t = descTable.get(n);
      // Empty tree means no items here
      if(t == null) { return 0; }
      return t.first().price; // first obtains the lowest priced item here
   }

   /* 
      e. FindMaxPrice(n): given an integer, find items whose description
      contains that number, and return highest price of those items.
      Return 0 if there is no such item.
   */
   public int findMaxPrice(int n) {
      // Check the tree at description n
      TreeSet<Item> t = descTable.get(n);
      // Empty tree means no items here
      if(t == null) { return 0; }
      return t.last().price; // last obtains the highest priced item here
   }

   /* 
      f. FindPriceRange(n,low,high): given int n, find the number
      of items whose description contains n, and in addition,
      their prices fall within the given range, [low, high].
   */
   public int findPriceRange(int n, int low, int high) {
      // Check the tree at description n
      TreeSet<Item> t = descTable.get(n);
      // Empty tree means no items here
      if(t == null) { return 0; }

      // Init sum
      int sum = 0;

      // Traverse the tree 
      for(Item i : t) {
         if(i.price < low) continue;
         if(i.price > high) break;
         sum++;
      }

      return sum;
   }

   /*
      g. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
   */
   public int removeNames(int id, java.util.List<Integer> list) {
      Item i = idTable.get(id);
      // Return 0 if no such item
      if(i == null || list == null) { return 0; }

      // List to contain elements that can be removed
      List<Integer> toRemove = new LinkedList<>();


      int sum = 0;

      // Create a list of items that can be successfully removed
      for(Integer e : list) {
         if(i.description.contains(e)) {
            toRemove.add(e);
            sum += e;
         }
      }

      // Update item by removing from description list
      if(!i.description.removeAll(toRemove)) {
         return 0; // Nothing to be removed if nothing gets removed
      }
      // Init a new item
      Item j = new Item(id, i.price, i.description);
      // Replace in id table
      idTable.put(id, j);

      // Update in the description hashtable
      // Remove old item from old descriptor trees
      for(Integer e : toRemove) {
         TreeSet<Item> t = descTable.get(e);
         // Remove from tree
         t.remove(i);
         if(t.size() == 0) {
            descTable.remove(e); // If tree is now empty, remove tree from desc table
         }
      }
      // Replace item in remaining trees
      for(Integer e : j.description) {
         TreeSet<Item> t = descTable.get(e);
         t.add(j);
      }

	   return sum;
   }
}

