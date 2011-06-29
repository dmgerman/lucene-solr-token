begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.collections
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An Array-based hashtable which maps primitive int to a primitive double.<br>  * The hashtable is constracted with a given capacity, or 16 as a default. In  * case there's not enough room for new pairs, the hashtable grows.<br>  * Capacity is adjusted to a power of 2, and there are 2 * capacity entries for  * the hash.  *   * The pre allocated arrays (for keys, values) are at length of capacity + 1,  * when index 0 is used as 'Ground' or 'NULL'.<br>  *   * The arrays are allocated ahead of hash operations, and form an 'empty space'  * list, to which the key,value pair is allocated.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntToDoubleMap
specifier|public
class|class
name|IntToDoubleMap
block|{
DECL|field|GROUND
specifier|public
specifier|static
specifier|final
name|double
name|GROUND
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
comment|/**    * Implements an IntIterator which iterates over all the allocated indexes.    */
DECL|class|IndexIterator
specifier|private
specifier|final
class|class
name|IndexIterator
implements|implements
name|IntIterator
block|{
comment|/**      * The last used baseHashIndex. Needed for "jumping" from one hash entry      * to another.      */
DECL|field|baseHashIndex
specifier|private
name|int
name|baseHashIndex
init|=
literal|0
decl_stmt|;
comment|/**      * The next not-yet-visited index.      */
DECL|field|index
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|/**      * Index of the last visited pair. Used in {@link #remove()}.      */
DECL|field|lastIndex
specifier|private
name|int
name|lastIndex
init|=
literal|0
decl_stmt|;
comment|/**      * Create the Iterator, make<code>index</code> point to the "first"      * index which is not empty. If such does not exist (eg. the map is      * empty) it would be zero.      */
DECL|method|IndexIterator
specifier|public
name|IndexIterator
parameter_list|()
block|{
for|for
control|(
name|baseHashIndex
operator|=
literal|0
init|;
name|baseHashIndex
operator|<
name|baseHash
operator|.
name|length
condition|;
operator|++
name|baseHashIndex
control|)
block|{
name|index
operator|=
name|baseHash
index|[
name|baseHashIndex
index|]
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|index
operator|!=
literal|0
operator|)
return|;
block|}
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
comment|// Save the last index visited
name|lastIndex
operator|=
name|index
expr_stmt|;
comment|// next the index
name|index
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
comment|// if the next index points to the 'Ground' it means we're done with
comment|// the current hash entry and we need to jump to the next one. This
comment|// is done until all the hash entries had been visited.
while|while
condition|(
name|index
operator|==
literal|0
operator|&&
operator|++
name|baseHashIndex
operator|<
name|baseHash
operator|.
name|length
condition|)
block|{
name|index
operator|=
name|baseHash
index|[
name|baseHashIndex
index|]
expr_stmt|;
block|}
return|return
name|lastIndex
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|IntToDoubleMap
operator|.
name|this
operator|.
name|remove
argument_list|(
name|keys
index|[
name|lastIndex
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implements an IntIterator, used for iteration over the map's keys.    */
DECL|class|KeyIterator
specifier|private
specifier|final
class|class
name|KeyIterator
implements|implements
name|IntIterator
block|{
DECL|field|iterator
specifier|private
name|IntIterator
name|iterator
init|=
operator|new
name|IndexIterator
argument_list|()
decl_stmt|;
DECL|method|KeyIterator
name|KeyIterator
parameter_list|()
block|{ }
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
return|return
name|keys
index|[
name|iterator
operator|.
name|next
argument_list|()
index|]
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Implements an Iterator of a generic type T used for iteration over the    * map's values.    */
DECL|class|ValueIterator
specifier|private
specifier|final
class|class
name|ValueIterator
implements|implements
name|DoubleIterator
block|{
DECL|field|iterator
specifier|private
name|IntIterator
name|iterator
init|=
operator|new
name|IndexIterator
argument_list|()
decl_stmt|;
DECL|method|ValueIterator
name|ValueIterator
parameter_list|()
block|{ }
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|double
name|next
parameter_list|()
block|{
return|return
name|values
index|[
name|iterator
operator|.
name|next
argument_list|()
index|]
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Default capacity - in case no capacity was specified in the constructor    */
DECL|field|defaultCapacity
specifier|private
specifier|static
name|int
name|defaultCapacity
init|=
literal|16
decl_stmt|;
comment|/**    * Holds the base hash entries. if the capacity is 2^N, than the base hash    * holds 2^(N+1). It can hold    */
DECL|field|baseHash
name|int
index|[]
name|baseHash
decl_stmt|;
comment|/**    * The current capacity of the map. Always 2^N and never less than 16. We    * never use the zero index. It is needed to improve performance and is also    * used as "ground".    */
DECL|field|capacity
specifier|private
name|int
name|capacity
decl_stmt|;
comment|/**    * All objects are being allocated at map creation. Those objects are "free"    * or empty. Whenever a new pair comes along, a pair is being "allocated" or    * taken from the free-linked list. as this is just a free list.    */
DECL|field|firstEmpty
specifier|private
name|int
name|firstEmpty
decl_stmt|;
comment|/**    * hashFactor is always (2^(N+1)) - 1. Used for faster hashing.    */
DECL|field|hashFactor
specifier|private
name|int
name|hashFactor
decl_stmt|;
comment|/**    * This array holds the unique keys    */
DECL|field|keys
name|int
index|[]
name|keys
decl_stmt|;
comment|/**    * In case of collisions, we implement a double linked list of the colliding    * hash's with the following next[] and prev[]. Those are also used to store    * the "empty" list.    */
DECL|field|next
name|int
index|[]
name|next
decl_stmt|;
DECL|field|prev
specifier|private
name|int
name|prev
decl_stmt|;
comment|/**    * Number of currently objects in the map.    */
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/**    * This array holds the values    */
DECL|field|values
name|double
index|[]
name|values
decl_stmt|;
comment|/**    * Constructs a map with default capacity.    */
DECL|method|IntToDoubleMap
specifier|public
name|IntToDoubleMap
parameter_list|()
block|{
name|this
argument_list|(
name|defaultCapacity
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a map with given capacity. Capacity is adjusted to a native    * power of 2, with minimum of 16.    *     * @param capacity    *            minimum capacity for the map.    */
DECL|method|IntToDoubleMap
specifier|public
name|IntToDoubleMap
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
literal|16
expr_stmt|;
comment|// Minimum capacity is 16..
while|while
condition|(
name|this
operator|.
name|capacity
operator|<
name|capacity
condition|)
block|{
comment|// Multiply by 2 as long as we're still under the requested capacity
name|this
operator|.
name|capacity
operator|<<=
literal|1
expr_stmt|;
block|}
comment|// As mentioned, we use the first index (0) as 'Ground', so we need the
comment|// length of the arrays to be one more than the capacity
name|int
name|arrayLength
init|=
name|this
operator|.
name|capacity
operator|+
literal|1
decl_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|double
index|[
name|arrayLength
index|]
expr_stmt|;
name|this
operator|.
name|keys
operator|=
operator|new
name|int
index|[
name|arrayLength
index|]
expr_stmt|;
name|this
operator|.
name|next
operator|=
operator|new
name|int
index|[
name|arrayLength
index|]
expr_stmt|;
comment|// Hash entries are twice as big as the capacity.
name|int
name|baseHashSize
init|=
name|this
operator|.
name|capacity
operator|<<
literal|1
decl_stmt|;
name|this
operator|.
name|baseHash
operator|=
operator|new
name|int
index|[
name|baseHashSize
index|]
expr_stmt|;
name|this
operator|.
name|values
index|[
literal|0
index|]
operator|=
name|GROUND
expr_stmt|;
comment|// The has factor is 2^M - 1 which is used as an "AND" hashing operator.
comment|// {@link #calcBaseHash()}
name|this
operator|.
name|hashFactor
operator|=
name|baseHashSize
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|size
operator|=
literal|0
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a pair to the map. Takes the first empty position from the    * empty-linked-list's head - {@link firstEmpty}.    *     * New pairs are always inserted to baseHash, and are followed by the old    * colliding pair.    *     * @param key    *            integer which maps the given Object    * @param v    *            double value which is being mapped using the given key    */
DECL|method|prvt_put
specifier|private
name|void
name|prvt_put
parameter_list|(
name|int
name|key
parameter_list|,
name|double
name|v
parameter_list|)
block|{
comment|// Hash entry to which the new pair would be inserted
name|int
name|hashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// 'Allocating' a pair from the "Empty" list.
name|int
name|objectIndex
init|=
name|firstEmpty
decl_stmt|;
comment|// Setting data
name|firstEmpty
operator|=
name|next
index|[
name|firstEmpty
index|]
expr_stmt|;
name|values
index|[
name|objectIndex
index|]
operator|=
name|v
expr_stmt|;
name|keys
index|[
name|objectIndex
index|]
operator|=
name|key
expr_stmt|;
comment|// Inserting the new pair as the first node in the specific hash entry
name|next
index|[
name|objectIndex
index|]
operator|=
name|baseHash
index|[
name|hashIndex
index|]
expr_stmt|;
name|baseHash
index|[
name|hashIndex
index|]
operator|=
name|objectIndex
expr_stmt|;
comment|// Announcing a new pair was added!
operator|++
name|size
expr_stmt|;
block|}
comment|/**    * Calculating the baseHash index using the internal<code>hashFactor</code>    * .    *     * @param key    */
DECL|method|calcBaseHashIndex
specifier|protected
name|int
name|calcBaseHashIndex
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|key
operator|&
name|hashFactor
return|;
block|}
comment|/**    * Empties the map. Generates the "Empty" space list for later allocation.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// Clears the hash entries
name|Arrays
operator|.
name|fill
argument_list|(
name|this
operator|.
name|baseHash
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Set size to zero
name|size
operator|=
literal|0
expr_stmt|;
comment|// Mark all array entries as empty. This is done with
comment|//<code>firstEmpty</code> pointing to the first valid index (1 as 0 is
comment|// used as 'Ground').
name|firstEmpty
operator|=
literal|1
expr_stmt|;
comment|// And setting all the<code>next[i]</code> to point at
comment|//<code>i+1</code>.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|this
operator|.
name|capacity
condition|;
control|)
block|{
name|next
index|[
name|i
index|]
operator|=
operator|++
name|i
expr_stmt|;
block|}
comment|// Surly, the last one should point to the 'Ground'.
name|next
index|[
name|this
operator|.
name|capacity
index|]
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Checks if a given key exists in the map.    *     * @param key    *            that is checked against the map data.    * @return true if the key exists in the map. false otherwise.    */
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|key
argument_list|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Checks if the given value exists in the map.<br>    * This method iterates over the collection, trying to find an equal object.    *     * @param value    *            double value that is checked against the map data.    * @return true if the value exists in the map, false otherwise.    */
DECL|method|containsValue
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
for|for
control|(
name|DoubleIterator
name|iterator
init|=
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|double
name|d
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|d
operator|==
name|value
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Find the actual index of a given key.    *     * @param key    * @return index of the key. zero if the key wasn't found.    */
DECL|method|find
specifier|protected
name|int
name|find
parameter_list|(
name|int
name|key
parameter_list|)
block|{
comment|// Calculate the hash entry.
name|int
name|baseHashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Start from the hash entry.
name|int
name|localIndex
init|=
name|baseHash
index|[
name|baseHashIndex
index|]
decl_stmt|;
comment|// while the index does not point to the 'Ground'
while|while
condition|(
name|localIndex
operator|!=
literal|0
condition|)
block|{
comment|// returns the index found in case of of a matching key.
if|if
condition|(
name|keys
index|[
name|localIndex
index|]
operator|==
name|key
condition|)
block|{
return|return
name|localIndex
return|;
block|}
comment|// next the local index
name|localIndex
operator|=
name|next
index|[
name|localIndex
index|]
expr_stmt|;
block|}
comment|// If we got this far, it could only mean we did not find the key we
comment|// were asked for. return 'Ground' index.
return|return
literal|0
return|;
block|}
comment|/**    * Find the actual index of a given key with it's baseHashIndex.<br>    * Some methods use the baseHashIndex. If those call {@link #find()} there's    * no need to re-calculate that hash.    *     * @param key    * @param baseHashIndex    * @return the index of the given key, or 0 as 'Ground' if the key wasn't    *         found.    */
DECL|method|findForRemove
specifier|private
name|int
name|findForRemove
parameter_list|(
name|int
name|key
parameter_list|,
name|int
name|baseHashIndex
parameter_list|)
block|{
comment|// Start from the hash entry.
name|this
operator|.
name|prev
operator|=
literal|0
expr_stmt|;
name|int
name|index
init|=
name|baseHash
index|[
name|baseHashIndex
index|]
decl_stmt|;
comment|// while the index does not point to the 'Ground'
while|while
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
comment|// returns the index found in case of of a matching key.
if|if
condition|(
name|keys
index|[
name|index
index|]
operator|==
name|key
condition|)
block|{
return|return
name|index
return|;
block|}
comment|// next the local index
name|prev
operator|=
name|index
expr_stmt|;
name|index
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
block|}
comment|// If we got this far, it could only mean we did not find the key we
comment|// were asked for. return 'Ground' index.
name|this
operator|.
name|prev
operator|=
literal|0
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/**    * Returns the value mapped with the given key.    *     * @param key    *            int who's mapped object we're interested in.    * @return a double value mapped by the given key. Double.NaN if the key wasn't found.    */
DECL|method|get
specifier|public
name|double
name|get
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|values
index|[
name|find
argument_list|(
name|key
argument_list|)
index|]
return|;
block|}
comment|/**    * Grows the map. Allocates a new map of double the capacity, and    * fast-insert the old key-value pairs.    */
DECL|method|grow
specifier|protected
name|void
name|grow
parameter_list|()
block|{
name|IntToDoubleMap
name|that
init|=
operator|new
name|IntToDoubleMap
argument_list|(
name|this
operator|.
name|capacity
operator|*
literal|2
argument_list|)
decl_stmt|;
comment|// Iterates fast over the collection. Any valid pair is put into the new
comment|// map without checking for duplicates or if there's enough space for
comment|// it.
for|for
control|(
name|IndexIterator
name|iterator
init|=
operator|new
name|IndexIterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|int
name|index
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|that
operator|.
name|prvt_put
argument_list|(
name|this
operator|.
name|keys
index|[
name|index
index|]
argument_list|,
name|this
operator|.
name|values
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Copy that's data into this.
name|this
operator|.
name|capacity
operator|=
name|that
operator|.
name|capacity
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|that
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|firstEmpty
operator|=
name|that
operator|.
name|firstEmpty
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|that
operator|.
name|values
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|that
operator|.
name|keys
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|that
operator|.
name|next
expr_stmt|;
name|this
operator|.
name|baseHash
operator|=
name|that
operator|.
name|baseHash
expr_stmt|;
name|this
operator|.
name|hashFactor
operator|=
name|that
operator|.
name|hashFactor
expr_stmt|;
block|}
comment|/**    *     * @return true if the map is empty. false otherwise.    */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|==
literal|0
return|;
block|}
comment|/**    * Returns a new iterator for the mapped double values.    */
DECL|method|iterator
specifier|public
name|DoubleIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ValueIterator
argument_list|()
return|;
block|}
comment|/** Returns an iterator on the map keys. */
DECL|method|keyIterator
specifier|public
name|IntIterator
name|keyIterator
parameter_list|()
block|{
return|return
operator|new
name|KeyIterator
argument_list|()
return|;
block|}
comment|/**    * Prints the baseHash array, used for debug purposes.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|printBaseHash
specifier|private
name|void
name|printBaseHash
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|baseHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|".\t"
operator|+
name|baseHash
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Inserts the&lt;key,value&gt; pair into the map. If the key already exists,    * this method updates the mapped value to the given one, returning the old    * mapped value.    *     * @return the old mapped value, or {@link Double#NaN} if the key didn't exist.    */
DECL|method|put
specifier|public
name|double
name|put
parameter_list|(
name|int
name|key
parameter_list|,
name|double
name|v
parameter_list|)
block|{
comment|// Does key exists?
name|int
name|index
init|=
name|find
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Yes!
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
comment|// Set new data and exit.
name|double
name|old
init|=
name|values
index|[
name|index
index|]
decl_stmt|;
name|values
index|[
name|index
index|]
operator|=
name|v
expr_stmt|;
return|return
name|old
return|;
block|}
comment|// Is there enough room for a new pair?
if|if
condition|(
name|size
operator|==
name|capacity
condition|)
block|{
comment|// No? Than grow up!
name|grow
argument_list|()
expr_stmt|;
block|}
comment|// Now that everything is set, the pair can be just put inside with no
comment|// worries.
name|prvt_put
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
return|return
name|Double
operator|.
name|NaN
return|;
block|}
comment|/**    * Removes a&lt;key,value&gt; pair from the map and returns the mapped value,    * or {@link Double#NaN} if the none existed.    *     * @param key used to find the value to remove    * @return the removed value or {@link Double#NaN} if none existed.    */
DECL|method|remove
specifier|public
name|double
name|remove
parameter_list|(
name|int
name|key
parameter_list|)
block|{
name|int
name|baseHashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|findForRemove
argument_list|(
name|key
argument_list|,
name|baseHashIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
comment|// If it is the first in the collision list, we should promote its
comment|// next colliding element.
if|if
condition|(
name|prev
operator|==
literal|0
condition|)
block|{
name|baseHash
index|[
name|baseHashIndex
index|]
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
block|}
name|next
index|[
name|prev
index|]
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
name|next
index|[
name|index
index|]
operator|=
name|firstEmpty
expr_stmt|;
name|firstEmpty
operator|=
name|index
expr_stmt|;
operator|--
name|size
expr_stmt|;
return|return
name|values
index|[
name|index
index|]
return|;
block|}
return|return
name|Double
operator|.
name|NaN
return|;
block|}
comment|/**    * @return number of pairs currently in the map    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|size
return|;
block|}
comment|/**    * Translates the mapped pairs' values into an array of Objects    *     * @return a double array of all the values currently in the map.    */
DECL|method|toArray
specifier|public
name|double
index|[]
name|toArray
parameter_list|()
block|{
name|int
name|j
init|=
operator|-
literal|1
decl_stmt|;
name|double
index|[]
name|array
init|=
operator|new
name|double
index|[
name|size
index|]
decl_stmt|;
comment|// Iterates over the values, adding them to the array.
for|for
control|(
name|DoubleIterator
name|iterator
init|=
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|array
index|[
operator|++
name|j
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
comment|/**    * Translates the mapped pairs' values into an array of T    *     * @param a    *            the array into which the elements of the list are to be    *            stored. If it is big enough use whatever space we need,    *            setting the one after the true data as {@link Double#NaN}.    *     * @return an array containing the elements of the list, using the given    *         parameter if big enough, otherwise allocate an appropriate array    *         and return it.    *     */
DECL|method|toArray
specifier|public
name|double
index|[]
name|toArray
parameter_list|(
name|double
index|[]
name|a
parameter_list|)
block|{
name|int
name|j
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|this
operator|.
name|size
argument_list|()
condition|)
block|{
name|a
operator|=
operator|new
name|double
index|[
name|this
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
comment|// Iterates over the values, adding them to the array.
for|for
control|(
name|DoubleIterator
name|iterator
init|=
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|a
index|[
name|j
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|<
name|a
operator|.
name|length
condition|)
block|{
name|a
index|[
name|j
index|]
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|IntIterator
name|keyIterator
init|=
name|keyIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keyIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|key
init|=
name|keyIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IntToDoubleMap
name|that
init|=
operator|(
name|IntToDoubleMap
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|that
operator|.
name|size
argument_list|()
operator|!=
name|this
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IntIterator
name|it
init|=
name|keyIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|key
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|that
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|double
name|v1
init|=
name|this
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|double
name|v2
init|=
name|that
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

