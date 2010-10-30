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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
operator|.
name|DocValues
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
name|index
operator|.
name|values
operator|.
name|MultiDocValues
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
name|index
operator|.
name|values
operator|.
name|Values
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
name|util
operator|.
name|PriorityQueue
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
name|util
operator|.
name|ReaderUtil
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
name|util
operator|.
name|ReaderUtil
operator|.
name|Slice
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments.  * This does a merge sort, by field name, of the  * sub-readers.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiFieldsEnum
specifier|public
specifier|final
class|class
name|MultiFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|queue
specifier|private
specifier|final
name|FieldMergeQueue
name|queue
decl_stmt|;
comment|// Holds sub-readers containing field we are currently
comment|// on, popped from queue.
DECL|field|top
specifier|private
specifier|final
name|FieldsEnumWithSlice
index|[]
name|top
decl_stmt|;
DECL|field|enumWithSlices
specifier|private
specifier|final
name|FieldsEnumWithSlice
index|[]
name|enumWithSlices
decl_stmt|;
DECL|field|numTop
specifier|private
name|int
name|numTop
decl_stmt|;
comment|// Re-used TermsEnum
DECL|field|terms
specifier|private
specifier|final
name|MultiTermsEnum
name|terms
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|MultiDocValues
name|docValues
decl_stmt|;
DECL|field|currentField
specifier|private
name|String
name|currentField
decl_stmt|;
comment|/** The subs array must be newly initialized FieldsEnum    *  (ie, {@link FieldsEnum#next} has not been called. */
DECL|method|MultiFieldsEnum
specifier|public
name|MultiFieldsEnum
parameter_list|(
name|FieldsEnum
index|[]
name|subs
parameter_list|,
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
parameter_list|)
throws|throws
name|IOException
block|{
name|terms
operator|=
operator|new
name|MultiTermsEnum
argument_list|(
name|subSlices
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|FieldMergeQueue
argument_list|(
name|subs
operator|.
name|length
argument_list|)
expr_stmt|;
name|docValues
operator|=
operator|new
name|MultiDocValues
argument_list|()
expr_stmt|;
name|top
operator|=
operator|new
name|FieldsEnumWithSlice
index|[
name|subs
operator|.
name|length
index|]
expr_stmt|;
name|List
argument_list|<
name|FieldsEnumWithSlice
argument_list|>
name|enumWithSlices
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldsEnumWithSlice
argument_list|>
argument_list|()
decl_stmt|;
comment|// Init q
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|subs
index|[
name|i
index|]
operator|!=
literal|null
assert|;
specifier|final
name|String
name|field
init|=
name|subs
index|[
name|i
index|]
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
comment|// this FieldsEnum has at least one field
specifier|final
name|FieldsEnumWithSlice
name|sub
init|=
operator|new
name|FieldsEnumWithSlice
argument_list|(
name|subs
index|[
name|i
index|]
argument_list|,
name|subSlices
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|enumWithSlices
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|sub
operator|.
name|current
operator|=
name|field
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|enumWithSlices
operator|=
name|enumWithSlices
operator|.
name|toArray
argument_list|(
name|FieldsEnumWithSlice
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// restore queue
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTop
condition|;
name|i
operator|++
control|)
block|{
name|top
index|[
name|i
index|]
operator|.
name|current
operator|=
name|top
index|[
name|i
index|]
operator|.
name|fields
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|top
index|[
name|i
index|]
operator|.
name|current
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|top
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no more fields in this sub-reader
block|}
block|}
name|numTop
operator|=
literal|0
expr_stmt|;
comment|// gather equal top fields
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|queue
operator|.
name|top
argument_list|()
operator|)
operator|.
name|current
operator|!=
name|top
index|[
literal|0
index|]
operator|.
name|current
condition|)
block|{
break|break;
block|}
block|}
name|currentField
operator|=
name|top
index|[
literal|0
index|]
operator|.
name|current
expr_stmt|;
block|}
else|else
block|{
name|currentField
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|currentField
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|>
name|termsEnums
init|=
operator|new
name|ArrayList
argument_list|<
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|>
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TermsEnum
name|terms
init|=
name|top
index|[
name|i
index|]
operator|.
name|fields
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnums
operator|.
name|add
argument_list|(
operator|new
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|(
name|terms
argument_list|,
name|top
index|[
name|i
index|]
operator|.
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnums
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
name|terms
operator|.
name|reset
argument_list|(
name|termsEnums
operator|.
name|toArray
argument_list|(
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|FieldsEnumWithSlice
specifier|public
specifier|final
specifier|static
class|class
name|FieldsEnumWithSlice
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|FieldsEnumWithSlice
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|FieldsEnumWithSlice
index|[
literal|0
index|]
decl_stmt|;
DECL|field|fields
specifier|final
name|FieldsEnum
name|fields
decl_stmt|;
DECL|field|slice
specifier|final
name|ReaderUtil
operator|.
name|Slice
name|slice
decl_stmt|;
DECL|field|index
specifier|final
name|int
name|index
decl_stmt|;
DECL|field|current
name|String
name|current
decl_stmt|;
DECL|method|FieldsEnumWithSlice
specifier|public
name|FieldsEnumWithSlice
parameter_list|(
name|FieldsEnum
name|fields
parameter_list|,
name|ReaderUtil
operator|.
name|Slice
name|slice
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|slice
operator|=
name|slice
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
assert|assert
name|slice
operator|.
name|length
operator|>=
literal|0
operator|:
literal|"length="
operator|+
name|slice
operator|.
name|length
assert|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
block|}
DECL|class|FieldMergeQueue
specifier|private
specifier|final
specifier|static
class|class
name|FieldMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FieldsEnumWithSlice
argument_list|>
block|{
DECL|method|FieldMergeQueue
name|FieldMergeQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|FieldsEnumWithSlice
name|fieldsA
parameter_list|,
name|FieldsEnumWithSlice
name|fieldsB
parameter_list|)
block|{
comment|// No need to break ties by field name: TermsEnum handles that
return|return
name|fieldsA
operator|.
name|current
operator|.
name|compareTo
argument_list|(
name|fieldsB
operator|.
name|current
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|>
name|docValuesIndex
init|=
operator|new
name|ArrayList
argument_list|<
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docsUpto
init|=
literal|0
decl_stmt|;
name|Values
name|type
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numEnums
init|=
name|enumWithSlices
operator|.
name|length
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
name|numEnums
condition|;
name|i
operator|++
control|)
block|{
name|FieldsEnumWithSlice
name|withSlice
init|=
name|enumWithSlices
index|[
name|i
index|]
decl_stmt|;
name|Slice
name|slice
init|=
name|withSlice
operator|.
name|slice
decl_stmt|;
specifier|final
name|DocValues
name|values
init|=
name|withSlice
operator|.
name|fields
operator|.
name|docValues
argument_list|()
decl_stmt|;
specifier|final
name|int
name|start
init|=
name|slice
operator|.
name|start
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|slice
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
operator|&&
name|currentField
operator|.
name|equals
argument_list|(
name|withSlice
operator|.
name|current
argument_list|)
condition|)
block|{
if|if
condition|(
name|docsUpto
operator|!=
name|start
condition|)
block|{
name|type
operator|=
name|values
operator|.
name|type
argument_list|()
expr_stmt|;
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DummyDocValues
argument_list|(
name|start
argument_list|,
name|type
argument_list|)
argument_list|,
name|docsUpto
argument_list|,
name|start
operator|-
name|docsUpto
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
name|values
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|docsUpto
operator|=
name|start
operator|+
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|+
literal|1
operator|==
name|numEnums
operator|&&
operator|!
name|docValuesIndex
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|docValuesIndex
operator|.
name|add
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DocValuesIndex
argument_list|(
operator|new
name|MultiDocValues
operator|.
name|DummyDocValues
argument_list|(
name|start
argument_list|,
name|type
argument_list|)
argument_list|,
name|docsUpto
argument_list|,
name|start
operator|-
name|docsUpto
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|docValuesIndex
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|docValues
operator|.
name|reset
argument_list|(
name|docValuesIndex
operator|.
name|toArray
argument_list|(
name|MultiDocValues
operator|.
name|DocValuesIndex
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

