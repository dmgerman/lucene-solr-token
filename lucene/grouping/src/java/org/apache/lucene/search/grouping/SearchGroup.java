begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldComparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
import|;
end_import

begin_comment
comment|/**  * Represents a group that is found during the first pass search.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SearchGroup
specifier|public
class|class
name|SearchGroup
parameter_list|<
name|T
parameter_list|>
block|{
comment|/** The value that defines this group  */
DECL|field|groupValue
specifier|public
name|T
name|groupValue
decl_stmt|;
comment|/** The sort values used during sorting. These are the    *  groupSort field values of the highest rank document    *  (by the groupSort) within the group.  Can be    *<code>null</code> if<code>fillFields=false</code> had    * been passed to {@link FirstPassGroupingCollector#getTopGroups} */
DECL|field|sortValues
specifier|public
name|Object
index|[]
name|sortValues
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
literal|"SearchGroup(groupValue="
operator|+
name|groupValue
operator|+
literal|" sortValues="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|sortValues
argument_list|)
operator|+
literal|")"
operator|)
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|SearchGroup
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|SearchGroup
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|that
operator|.
name|groupValue
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|groupValue
operator|.
name|equals
argument_list|(
name|that
operator|.
name|groupValue
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
name|groupValue
operator|!=
literal|null
condition|?
name|groupValue
operator|.
name|hashCode
argument_list|()
else|:
literal|0
return|;
block|}
DECL|class|ShardIter
specifier|private
specifier|static
class|class
name|ShardIter
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|iter
specifier|public
specifier|final
name|Iterator
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|iter
decl_stmt|;
DECL|field|shardIndex
specifier|public
specifier|final
name|int
name|shardIndex
decl_stmt|;
DECL|method|ShardIter
specifier|public
name|ShardIter
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|shard
parameter_list|,
name|int
name|shardIndex
parameter_list|)
block|{
name|this
operator|.
name|shardIndex
operator|=
name|shardIndex
expr_stmt|;
name|iter
operator|=
name|shard
operator|.
name|iterator
argument_list|()
expr_stmt|;
assert|assert
name|iter
operator|.
name|hasNext
argument_list|()
assert|;
block|}
DECL|method|next
specifier|public
name|SearchGroup
argument_list|<
name|T
argument_list|>
name|next
parameter_list|()
block|{
assert|assert
name|iter
operator|.
name|hasNext
argument_list|()
assert|;
specifier|final
name|SearchGroup
argument_list|<
name|T
argument_list|>
name|group
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|sortValues
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"group.sortValues is null; you must pass fillFields=true to the first pass collector"
argument_list|)
throw|;
block|}
return|return
name|group
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
return|return
literal|"ShardIter(shard="
operator|+
name|shardIndex
operator|+
literal|")"
return|;
block|}
block|}
comment|// Holds all shards currently on the same group
DECL|class|MergedGroup
specifier|private
specifier|static
class|class
name|MergedGroup
parameter_list|<
name|T
parameter_list|>
block|{
comment|// groupValue may be null!
DECL|field|groupValue
specifier|public
specifier|final
name|T
name|groupValue
decl_stmt|;
DECL|field|topValues
specifier|public
name|Object
index|[]
name|topValues
decl_stmt|;
DECL|field|shards
specifier|public
specifier|final
name|List
argument_list|<
name|ShardIter
argument_list|<
name|T
argument_list|>
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|minShardIndex
specifier|public
name|int
name|minShardIndex
decl_stmt|;
DECL|field|processed
specifier|public
name|boolean
name|processed
decl_stmt|;
DECL|field|inQueue
specifier|public
name|boolean
name|inQueue
decl_stmt|;
DECL|method|MergedGroup
specifier|public
name|MergedGroup
parameter_list|(
name|T
name|groupValue
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
block|}
comment|// Only for assert
DECL|method|neverEquals
specifier|private
name|boolean
name|neverEquals
parameter_list|(
name|Object
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|instanceof
name|MergedGroup
condition|)
block|{
name|MergedGroup
argument_list|<
name|?
argument_list|>
name|other
init|=
operator|(
name|MergedGroup
argument_list|<
name|?
argument_list|>
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
assert|assert
name|other
operator|.
name|groupValue
operator|!=
literal|null
assert|;
block|}
else|else
block|{
assert|assert
operator|!
name|groupValue
operator|.
name|equals
argument_list|(
name|other
operator|.
name|groupValue
argument_list|)
assert|;
block|}
block|}
return|return
literal|true
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
name|_other
parameter_list|)
block|{
comment|// We never have another MergedGroup instance with
comment|// same groupValue
assert|assert
name|neverEquals
argument_list|(
name|_other
argument_list|)
assert|;
if|if
condition|(
name|_other
operator|instanceof
name|MergedGroup
condition|)
block|{
name|MergedGroup
argument_list|<
name|?
argument_list|>
name|other
init|=
operator|(
name|MergedGroup
argument_list|<
name|?
argument_list|>
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
return|return
name|other
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|groupValue
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|groupValue
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
DECL|class|GroupComparator
specifier|private
specifier|static
class|class
name|GroupComparator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Comparator
argument_list|<
name|MergedGroup
argument_list|<
name|T
argument_list|>
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|comparators
specifier|public
specifier|final
name|FieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reversed
specifier|public
specifier|final
name|int
index|[]
name|reversed
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|GroupComparator
specifier|public
name|GroupComparator
parameter_list|(
name|Sort
name|groupSort
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
name|reversed
operator|=
operator|new
name|int
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|sortFields
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|compIDX
index|]
decl_stmt|;
name|comparators
index|[
name|compIDX
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|compIDX
argument_list|)
expr_stmt|;
name|reversed
index|[
name|compIDX
index|]
operator|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|MergedGroup
argument_list|<
name|T
argument_list|>
name|group
parameter_list|,
name|MergedGroup
argument_list|<
name|T
argument_list|>
name|other
parameter_list|)
block|{
if|if
condition|(
name|group
operator|==
name|other
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|//System.out.println("compare group=" + group + " other=" + other);
specifier|final
name|Object
index|[]
name|groupValues
init|=
name|group
operator|.
name|topValues
decl_stmt|;
specifier|final
name|Object
index|[]
name|otherValues
init|=
name|other
operator|.
name|topValues
decl_stmt|;
comment|//System.out.println("  groupValues=" + groupValues + " otherValues=" + otherValues);
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareValues
argument_list|(
name|groupValues
index|[
name|compIDX
index|]
argument_list|,
name|otherValues
index|[
name|compIDX
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
comment|// Tie break by min shard index:
assert|assert
name|group
operator|.
name|minShardIndex
operator|!=
name|other
operator|.
name|minShardIndex
assert|;
return|return
name|group
operator|.
name|minShardIndex
operator|-
name|other
operator|.
name|minShardIndex
return|;
block|}
block|}
DECL|class|GroupMerger
specifier|private
specifier|static
class|class
name|GroupMerger
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|groupComp
specifier|private
specifier|final
name|GroupComparator
argument_list|<
name|T
argument_list|>
name|groupComp
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|NavigableSet
argument_list|<
name|MergedGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|queue
decl_stmt|;
DECL|field|groupsSeen
specifier|private
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|MergedGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|groupsSeen
decl_stmt|;
DECL|method|GroupMerger
specifier|public
name|GroupMerger
parameter_list|(
name|Sort
name|groupSort
parameter_list|)
throws|throws
name|IOException
block|{
name|groupComp
operator|=
operator|new
name|GroupComparator
argument_list|<>
argument_list|(
name|groupSort
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|groupComp
argument_list|)
expr_stmt|;
name|groupsSeen
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|updateNextGroup
specifier|private
name|void
name|updateNextGroup
parameter_list|(
name|int
name|topN
parameter_list|,
name|ShardIter
argument_list|<
name|T
argument_list|>
name|shard
parameter_list|)
block|{
while|while
condition|(
name|shard
operator|.
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|SearchGroup
argument_list|<
name|T
argument_list|>
name|group
init|=
name|shard
operator|.
name|next
argument_list|()
decl_stmt|;
name|MergedGroup
argument_list|<
name|T
argument_list|>
name|mergedGroup
init|=
name|groupsSeen
operator|.
name|get
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isNew
init|=
name|mergedGroup
operator|==
literal|null
decl_stmt|;
comment|//System.out.println("    next group=" + (group.groupValue == null ? "null" : ((BytesRef) group.groupValue).utf8ToString()) + " sort=" + Arrays.toString(group.sortValues));
if|if
condition|(
name|isNew
condition|)
block|{
comment|// Start a new group:
comment|//System.out.println("      new");
name|mergedGroup
operator|=
operator|new
name|MergedGroup
argument_list|<>
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
expr_stmt|;
name|mergedGroup
operator|.
name|minShardIndex
operator|=
name|shard
operator|.
name|shardIndex
expr_stmt|;
assert|assert
name|group
operator|.
name|sortValues
operator|!=
literal|null
assert|;
name|mergedGroup
operator|.
name|topValues
operator|=
name|group
operator|.
name|sortValues
expr_stmt|;
name|groupsSeen
operator|.
name|put
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
name|mergedGroup
argument_list|)
expr_stmt|;
name|mergedGroup
operator|.
name|inQueue
operator|=
literal|true
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|mergedGroup
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mergedGroup
operator|.
name|processed
condition|)
block|{
comment|// This shard produced a group that we already
comment|// processed; move on to next group...
continue|continue;
block|}
else|else
block|{
comment|//System.out.println("      old");
name|boolean
name|competes
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|groupComp
operator|.
name|comparators
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|cmp
init|=
name|groupComp
operator|.
name|reversed
index|[
name|compIDX
index|]
operator|*
name|groupComp
operator|.
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareValues
argument_list|(
name|group
operator|.
name|sortValues
index|[
name|compIDX
index|]
argument_list|,
name|mergedGroup
operator|.
name|topValues
index|[
name|compIDX
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// Definitely competes
name|competes
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
comment|// Definitely does not compete
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|groupComp
operator|.
name|comparators
operator|.
name|length
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|shardIndex
operator|<
name|mergedGroup
operator|.
name|minShardIndex
condition|)
block|{
name|competes
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
comment|//System.out.println("      competes=" + competes);
if|if
condition|(
name|competes
condition|)
block|{
comment|// Group's sort changed -- remove& re-insert
if|if
condition|(
name|mergedGroup
operator|.
name|inQueue
condition|)
block|{
name|queue
operator|.
name|remove
argument_list|(
name|mergedGroup
argument_list|)
expr_stmt|;
block|}
name|mergedGroup
operator|.
name|topValues
operator|=
name|group
operator|.
name|sortValues
expr_stmt|;
name|mergedGroup
operator|.
name|minShardIndex
operator|=
name|shard
operator|.
name|shardIndex
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|mergedGroup
argument_list|)
expr_stmt|;
name|mergedGroup
operator|.
name|inQueue
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|mergedGroup
operator|.
name|shards
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Prune un-competitive groups:
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
name|topN
condition|)
block|{
specifier|final
name|MergedGroup
argument_list|<
name|T
argument_list|>
name|group
init|=
name|queue
operator|.
name|pollLast
argument_list|()
decl_stmt|;
comment|//System.out.println("PRUNE: " + group);
name|group
operator|.
name|inQueue
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|merge
specifier|public
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|merge
parameter_list|(
name|List
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|shards
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|topN
parameter_list|)
block|{
specifier|final
name|int
name|maxQueueSize
init|=
name|offset
operator|+
name|topN
decl_stmt|;
comment|//System.out.println("merge");
comment|// Init queue:
for|for
control|(
name|int
name|shardIDX
init|=
literal|0
init|;
name|shardIDX
operator|<
name|shards
operator|.
name|size
argument_list|()
condition|;
name|shardIDX
operator|++
control|)
block|{
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|shard
init|=
name|shards
operator|.
name|get
argument_list|(
name|shardIDX
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|shard
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//System.out.println("  insert shard=" + shardIDX);
name|updateNextGroup
argument_list|(
name|maxQueueSize
argument_list|,
operator|new
name|ShardIter
argument_list|<>
argument_list|(
name|shard
argument_list|,
name|shardIDX
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Pull merged topN groups:
specifier|final
name|List
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|newTopGroups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|topN
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|MergedGroup
argument_list|<
name|T
argument_list|>
name|group
init|=
name|queue
operator|.
name|pollFirst
argument_list|()
decl_stmt|;
name|group
operator|.
name|processed
operator|=
literal|true
expr_stmt|;
comment|//System.out.println("  pop: shards=" + group.shards + " group=" + (group.groupValue == null ? "null" : (((BytesRef) group.groupValue).utf8ToString())) + " sortValues=" + Arrays.toString(group.topValues));
if|if
condition|(
name|count
operator|++
operator|>=
name|offset
condition|)
block|{
specifier|final
name|SearchGroup
argument_list|<
name|T
argument_list|>
name|newGroup
init|=
operator|new
name|SearchGroup
argument_list|<>
argument_list|()
decl_stmt|;
name|newGroup
operator|.
name|groupValue
operator|=
name|group
operator|.
name|groupValue
expr_stmt|;
name|newGroup
operator|.
name|sortValues
operator|=
name|group
operator|.
name|topValues
expr_stmt|;
name|newTopGroups
operator|.
name|add
argument_list|(
name|newGroup
argument_list|)
expr_stmt|;
if|if
condition|(
name|newTopGroups
operator|.
name|size
argument_list|()
operator|==
name|topN
condition|)
block|{
break|break;
block|}
comment|//} else {
comment|// System.out.println("    skip< offset");
block|}
comment|// Advance all iters in this group:
for|for
control|(
name|ShardIter
argument_list|<
name|T
argument_list|>
name|shardIter
range|:
name|group
operator|.
name|shards
control|)
block|{
name|updateNextGroup
argument_list|(
name|maxQueueSize
argument_list|,
name|shardIter
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newTopGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|newTopGroups
return|;
block|}
block|}
block|}
comment|/** Merges multiple collections of top groups, for example    *  obtained from separate index shards.  The provided    *  groupSort must match how the groups were sorted, and    *  the provided SearchGroups must have been computed    *  with fillFields=true passed to {@link    *  FirstPassGroupingCollector#getTopGroups}.    *    *<p>NOTE: this returns null if the topGroups is empty.    */
DECL|method|merge
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|merge
parameter_list|(
name|List
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|topGroups
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|topN
parameter_list|,
name|Sort
name|groupSort
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|GroupMerger
argument_list|<
name|T
argument_list|>
argument_list|(
name|groupSort
argument_list|)
operator|.
name|merge
argument_list|(
name|topGroups
argument_list|,
name|offset
argument_list|,
name|topN
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

