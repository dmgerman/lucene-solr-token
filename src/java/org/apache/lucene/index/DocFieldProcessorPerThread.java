begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|HashSet
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
name|io
operator|.
name|IOException
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Fieldable
import|;
end_import

begin_comment
comment|/**  * Gathers all Fieldables for a document under the same  * name, updates FieldInfos, and calls per-field consumers  * to process field by field.  *  * Currently, only a single thread visits the fields,  * sequentially, for processing.  */
end_comment

begin_class
DECL|class|DocFieldProcessorPerThread
specifier|final
class|class
name|DocFieldProcessorPerThread
extends|extends
name|DocConsumerPerThread
block|{
DECL|field|docBoost
name|float
name|docBoost
decl_stmt|;
DECL|field|fieldGen
name|int
name|fieldGen
decl_stmt|;
DECL|field|docFieldProcessor
specifier|final
name|DocFieldProcessor
name|docFieldProcessor
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|consumer
specifier|final
name|DocFieldConsumerPerThread
name|consumer
decl_stmt|;
comment|// Holds all fields seen in current doc
DECL|field|fields
name|DocFieldProcessorPerField
index|[]
name|fields
init|=
operator|new
name|DocFieldProcessorPerField
index|[
literal|1
index|]
decl_stmt|;
DECL|field|fieldCount
name|int
name|fieldCount
decl_stmt|;
comment|// Hash table for all fields ever seen
DECL|field|fieldHash
name|DocFieldProcessorPerField
index|[]
name|fieldHash
init|=
operator|new
name|DocFieldProcessorPerField
index|[
literal|2
index|]
decl_stmt|;
DECL|field|hashMask
name|int
name|hashMask
init|=
literal|1
decl_stmt|;
DECL|field|totalFieldCount
name|int
name|totalFieldCount
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|method|DocFieldProcessorPerThread
specifier|public
name|DocFieldProcessorPerThread
parameter_list|(
name|DocumentsWriterThreadState
name|threadState
parameter_list|,
name|DocFieldProcessor
name|docFieldProcessor
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docState
operator|=
name|threadState
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|docFieldProcessor
operator|=
name|docFieldProcessor
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|docFieldProcessor
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|docFieldProcessor
operator|.
name|consumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
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
name|fieldHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|field
init|=
name|fieldHash
index|[
name|i
index|]
decl_stmt|;
while|while
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocFieldProcessorPerField
name|next
init|=
name|field
operator|.
name|next
decl_stmt|;
name|field
operator|.
name|abort
argument_list|()
expr_stmt|;
name|field
operator|=
name|next
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
DECL|method|fields
specifier|public
name|Collection
name|fields
parameter_list|()
block|{
name|Collection
name|fields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|field
init|=
name|fieldHash
index|[
name|i
index|]
decl_stmt|;
while|while
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
operator|.
name|consumer
argument_list|)
expr_stmt|;
name|field
operator|=
name|field
operator|.
name|next
expr_stmt|;
block|}
block|}
assert|assert
name|fields
operator|.
name|size
argument_list|()
operator|==
name|totalFieldCount
assert|;
return|return
name|fields
return|;
block|}
comment|/** If there are fields we've seen but did not see again    *  in the last run, then free them up. */
DECL|method|trimFields
name|void
name|trimFields
parameter_list|(
name|DocumentsWriter
operator|.
name|FlushState
name|state
parameter_list|)
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
name|fieldHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|perField
init|=
name|fieldHash
index|[
name|i
index|]
decl_stmt|;
name|DocFieldProcessorPerField
name|lastPerField
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|perField
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|perField
operator|.
name|lastGen
operator|==
operator|-
literal|1
condition|)
block|{
comment|// This field was not seen since the previous
comment|// flush, so, free up its resources now
comment|// Unhash
if|if
condition|(
name|lastPerField
operator|==
literal|null
condition|)
name|fieldHash
index|[
name|i
index|]
operator|=
name|perField
operator|.
name|next
expr_stmt|;
else|else
name|lastPerField
operator|.
name|next
operator|=
name|perField
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|docWriter
operator|.
name|infoStream
operator|!=
literal|null
condition|)
name|state
operator|.
name|docWriter
operator|.
name|infoStream
operator|.
name|println
argument_list|(
literal|"  purge field="
operator|+
name|perField
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
name|totalFieldCount
operator|--
expr_stmt|;
block|}
else|else
block|{
comment|// Reset
name|perField
operator|.
name|lastGen
operator|=
operator|-
literal|1
expr_stmt|;
name|lastPerField
operator|=
name|perField
expr_stmt|;
block|}
name|perField
operator|=
name|perField
operator|.
name|next
expr_stmt|;
block|}
block|}
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
specifier|final
name|int
name|newHashSize
init|=
call|(
name|int
call|)
argument_list|(
name|fieldHash
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
assert|assert
name|newHashSize
operator|>
name|fieldHash
operator|.
name|length
assert|;
specifier|final
name|DocFieldProcessorPerField
name|newHashArray
index|[]
init|=
operator|new
name|DocFieldProcessorPerField
index|[
name|newHashSize
index|]
decl_stmt|;
comment|// Rehash
name|int
name|newHashMask
init|=
name|newHashSize
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fieldHash
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|fp0
init|=
name|fieldHash
index|[
name|j
index|]
decl_stmt|;
while|while
condition|(
name|fp0
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|hashPos2
init|=
name|fp0
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|hashCode
argument_list|()
operator|&
name|newHashMask
decl_stmt|;
name|DocFieldProcessorPerField
name|nextFP0
init|=
name|fp0
operator|.
name|next
decl_stmt|;
name|fp0
operator|.
name|next
operator|=
name|newHashArray
index|[
name|hashPos2
index|]
expr_stmt|;
name|newHashArray
index|[
name|hashPos2
index|]
operator|=
name|fp0
expr_stmt|;
name|fp0
operator|=
name|nextFP0
expr_stmt|;
block|}
block|}
name|fieldHash
operator|=
name|newHashArray
expr_stmt|;
name|hashMask
operator|=
name|newHashMask
expr_stmt|;
block|}
DECL|method|processDocument
specifier|public
name|DocumentsWriter
operator|.
name|DocWriter
name|processDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
specifier|final
name|Document
name|doc
init|=
name|docState
operator|.
name|doc
decl_stmt|;
assert|assert
name|docFieldProcessor
operator|.
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"DocumentsWriter.ThreadState.init start"
argument_list|)
assert|;
name|fieldCount
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|thisFieldGen
init|=
name|fieldGen
operator|++
decl_stmt|;
specifier|final
name|List
name|docFields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocFields
init|=
name|docFields
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Absorb any new fields first seen in this document.
comment|// Also absorb any changes to fields we had already
comment|// seen before (eg suddenly turning on norms or
comment|// vectors, etc.):
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocFields
condition|;
name|i
operator|++
control|)
block|{
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|docFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
comment|// Make sure we have a PerField allocated
specifier|final
name|int
name|hashPos
init|=
name|fieldName
operator|.
name|hashCode
argument_list|()
operator|&
name|hashMask
decl_stmt|;
name|DocFieldProcessorPerField
name|fp
init|=
name|fieldHash
index|[
name|hashPos
index|]
decl_stmt|;
while|while
condition|(
name|fp
operator|!=
literal|null
operator|&&
operator|!
name|fp
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
name|fp
operator|=
name|fp
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
comment|// TODO FI: we need to genericize the "flags" that a
comment|// field holds, and, how these flags are merged; it
comment|// needs to be more "pluggable" such that if I want
comment|// to have a new "thing" my Fields can do, I can
comment|// easily add it
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|field
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|field
operator|.
name|isTermVectorStored
argument_list|()
argument_list|,
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|getOmitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|field
operator|.
name|getOmitTf
argument_list|()
argument_list|)
decl_stmt|;
name|fp
operator|=
operator|new
name|DocFieldProcessorPerField
argument_list|(
name|this
argument_list|,
name|fi
argument_list|)
expr_stmt|;
name|fp
operator|.
name|next
operator|=
name|fieldHash
index|[
name|hashPos
index|]
expr_stmt|;
name|fieldHash
index|[
name|hashPos
index|]
operator|=
name|fp
expr_stmt|;
name|totalFieldCount
operator|++
expr_stmt|;
if|if
condition|(
name|totalFieldCount
operator|>=
name|fieldHash
operator|.
name|length
operator|/
literal|2
condition|)
name|rehash
argument_list|()
expr_stmt|;
block|}
else|else
name|fp
operator|.
name|fieldInfo
operator|.
name|update
argument_list|(
name|field
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|field
operator|.
name|isTermVectorStored
argument_list|()
argument_list|,
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|getOmitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|field
operator|.
name|getOmitTf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisFieldGen
operator|!=
name|fp
operator|.
name|lastGen
condition|)
block|{
comment|// First time we're seeing this field for this doc
name|fp
operator|.
name|fieldCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|fieldCount
operator|==
name|fields
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newSize
init|=
name|fields
operator|.
name|length
operator|*
literal|2
decl_stmt|;
name|DocFieldProcessorPerField
name|newArray
index|[]
init|=
operator|new
name|DocFieldProcessorPerField
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|fieldCount
argument_list|)
expr_stmt|;
name|fields
operator|=
name|newArray
expr_stmt|;
block|}
name|fields
index|[
name|fieldCount
operator|++
index|]
operator|=
name|fp
expr_stmt|;
name|fp
operator|.
name|lastGen
operator|=
name|thisFieldGen
expr_stmt|;
block|}
if|if
condition|(
name|fp
operator|.
name|fieldCount
operator|==
name|fp
operator|.
name|fields
operator|.
name|length
condition|)
block|{
name|Fieldable
index|[]
name|newArray
init|=
operator|new
name|Fieldable
index|[
name|fp
operator|.
name|fields
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fp
operator|.
name|fields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|fp
operator|.
name|fieldCount
argument_list|)
expr_stmt|;
name|fp
operator|.
name|fields
operator|=
name|newArray
expr_stmt|;
block|}
name|fp
operator|.
name|fields
index|[
name|fp
operator|.
name|fieldCount
operator|++
index|]
operator|=
name|field
expr_stmt|;
block|}
comment|// If we are writing vectors then we must visit
comment|// fields in sorted order so they are written in
comment|// sorted order.  TODO: we actually only need to
comment|// sort the subset of fields that have vectors
comment|// enabled; we could save [small amount of] CPU
comment|// here.
name|quickSort
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|fieldCount
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldCount
condition|;
name|i
operator|++
control|)
name|fields
index|[
name|i
index|]
operator|.
name|consumer
operator|.
name|processFields
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|fields
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|fieldCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|docState
operator|.
name|maxTermPrefix
operator|!=
literal|null
operator|&&
name|docState
operator|.
name|infoStream
operator|!=
literal|null
condition|)
name|docState
operator|.
name|infoStream
operator|.
name|println
argument_list|(
literal|"WARNING: document contains at least one immense term (longer than the max length "
operator|+
name|DocumentsWriter
operator|.
name|MAX_TERM_LENGTH
operator|+
literal|"), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '"
operator|+
name|docState
operator|.
name|maxTermPrefix
operator|+
literal|"...'"
argument_list|)
expr_stmt|;
return|return
name|consumer
operator|.
name|finishDocument
argument_list|()
return|;
block|}
DECL|method|quickSort
name|void
name|quickSort
parameter_list|(
name|DocFieldProcessorPerField
index|[]
name|array
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|lo
operator|>=
name|hi
condition|)
return|return;
elseif|else
if|if
condition|(
name|hi
operator|==
literal|1
operator|+
name|lo
condition|)
block|{
if|if
condition|(
name|array
index|[
name|lo
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|array
index|[
name|hi
index|]
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|>
literal|0
condition|)
block|{
specifier|final
name|DocFieldProcessorPerField
name|tmp
init|=
name|array
index|[
name|lo
index|]
decl_stmt|;
name|array
index|[
name|lo
index|]
operator|=
name|array
index|[
name|hi
index|]
expr_stmt|;
name|array
index|[
name|hi
index|]
operator|=
name|tmp
expr_stmt|;
block|}
return|return;
block|}
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
if|if
condition|(
name|array
index|[
name|lo
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|array
index|[
name|mid
index|]
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|>
literal|0
condition|)
block|{
name|DocFieldProcessorPerField
name|tmp
init|=
name|array
index|[
name|lo
index|]
decl_stmt|;
name|array
index|[
name|lo
index|]
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
name|array
index|[
name|mid
index|]
operator|=
name|tmp
expr_stmt|;
block|}
if|if
condition|(
name|array
index|[
name|mid
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|array
index|[
name|hi
index|]
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|>
literal|0
condition|)
block|{
name|DocFieldProcessorPerField
name|tmp
init|=
name|array
index|[
name|mid
index|]
decl_stmt|;
name|array
index|[
name|mid
index|]
operator|=
name|array
index|[
name|hi
index|]
expr_stmt|;
name|array
index|[
name|hi
index|]
operator|=
name|tmp
expr_stmt|;
if|if
condition|(
name|array
index|[
name|lo
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|array
index|[
name|mid
index|]
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|>
literal|0
condition|)
block|{
name|DocFieldProcessorPerField
name|tmp2
init|=
name|array
index|[
name|lo
index|]
decl_stmt|;
name|array
index|[
name|lo
index|]
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
name|array
index|[
name|mid
index|]
operator|=
name|tmp2
expr_stmt|;
block|}
block|}
name|int
name|left
init|=
name|lo
operator|+
literal|1
decl_stmt|;
name|int
name|right
init|=
name|hi
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|left
operator|>=
name|right
condition|)
return|return;
name|DocFieldProcessorPerField
name|partition
init|=
name|array
index|[
name|mid
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
while|while
condition|(
name|array
index|[
name|right
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|partition
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|>
literal|0
condition|)
operator|--
name|right
expr_stmt|;
while|while
condition|(
name|left
operator|<
name|right
operator|&&
name|array
index|[
name|left
index|]
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|partition
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
operator|<=
literal|0
condition|)
operator|++
name|left
expr_stmt|;
if|if
condition|(
name|left
operator|<
name|right
condition|)
block|{
name|DocFieldProcessorPerField
name|tmp
init|=
name|array
index|[
name|left
index|]
decl_stmt|;
name|array
index|[
name|left
index|]
operator|=
name|array
index|[
name|right
index|]
expr_stmt|;
name|array
index|[
name|right
index|]
operator|=
name|tmp
expr_stmt|;
operator|--
name|right
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|quickSort
argument_list|(
name|array
argument_list|,
name|lo
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|quickSort
argument_list|(
name|array
argument_list|,
name|left
operator|+
literal|1
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

