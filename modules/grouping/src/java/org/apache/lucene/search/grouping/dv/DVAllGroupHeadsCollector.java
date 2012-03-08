begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping.dv
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
operator|.
name|dv
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AtomicReaderContext
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
name|DocValues
operator|.
name|Type
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
name|Scorer
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
name|grouping
operator|.
name|AbstractAllGroupHeadsCollector
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
name|BytesRef
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
name|Collection
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
name|Map
import|;
end_import

begin_comment
comment|/**  * A base implementation of {@link org.apache.lucene.search.grouping.AbstractAllGroupHeadsCollector} for retrieving  * the most relevant groups when grouping on a indexed doc values field.  *  * @lucene.experimental  */
end_comment

begin_comment
comment|//TODO - (MvG): Add more optimized implementations
end_comment

begin_class
DECL|class|DVAllGroupHeadsCollector
specifier|public
specifier|abstract
class|class
name|DVAllGroupHeadsCollector
parameter_list|<
name|GH
extends|extends
name|AbstractAllGroupHeadsCollector
operator|.
name|GroupHead
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|AbstractAllGroupHeadsCollector
argument_list|<
name|GH
argument_list|>
block|{
DECL|field|groupField
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|diskResident
specifier|final
name|boolean
name|diskResident
decl_stmt|;
DECL|field|valueType
specifier|final
name|DocValues
operator|.
name|Type
name|valueType
decl_stmt|;
DECL|field|scratchBytesRef
specifier|final
name|BytesRef
name|scratchBytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|readerContext
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|method|DVAllGroupHeadsCollector
name|DVAllGroupHeadsCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|int
name|numberOfSorts
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
block|{
name|super
argument_list|(
name|numberOfSorts
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
name|this
operator|.
name|diskResident
operator|=
name|diskResident
expr_stmt|;
block|}
comment|/**    * Creates an<code>AbstractAllGroupHeadsCollector</code> instance based on the supplied arguments.    * This factory method decides with implementation is best suited.    *    * @param groupField      The field to group by    * @param sortWithinGroup The sort within each group    * @param type The {@link Type} which is used to select a concrete implementation.    * @param diskResident Whether the values to group by should be disk resident    * @return an<code>AbstractAllGroupHeadsCollector</code> instance based on the supplied arguments    * @throws IOException If I/O related errors occur    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|create
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|AbstractAllGroupHeadsCollector
operator|.
name|GroupHead
argument_list|<
name|?
argument_list|>
parameter_list|>
name|DVAllGroupHeadsCollector
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
comment|// Type erasure b/c otherwise we have inconvertible types...
return|return
operator|(
name|DVAllGroupHeadsCollector
operator|)
operator|new
name|GeneralAllGroupHeadsCollector
operator|.
name|Lng
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
return|return
operator|(
name|DVAllGroupHeadsCollector
operator|)
operator|new
name|GeneralAllGroupHeadsCollector
operator|.
name|Dbl
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
return|return
operator|(
name|DVAllGroupHeadsCollector
operator|)
operator|new
name|GeneralAllGroupHeadsCollector
operator|.
name|BR
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
operator|(
name|DVAllGroupHeadsCollector
operator|)
operator|new
name|GeneralAllGroupHeadsCollector
operator|.
name|SortedBR
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"ValueType %s not supported"
argument_list|,
name|type
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|GroupHead
specifier|static
class|class
name|GroupHead
extends|extends
name|AbstractAllGroupHeadsCollector
operator|.
name|GroupHead
argument_list|<
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|comparators
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
index|[]
name|comparators
decl_stmt|;
DECL|field|readerContext
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|method|GroupHead
name|GroupHead
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|groupValue
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|doc
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupValue
argument_list|,
name|doc
operator|+
name|readerContext
operator|.
name|docBase
argument_list|)
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
argument_list|<
name|?
argument_list|>
index|[
name|sortFields
operator|.
name|length
index|]
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|=
name|sortFields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|compIDX
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|updateDocHead
specifier|public
name|void
name|updateDocHead
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|comparator
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|doc
operator|=
name|doc
operator|+
name|readerContext
operator|.
name|docBase
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
specifier|final
name|DocValues
name|dv
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
operator|.
name|Source
name|dvSource
decl_stmt|;
if|if
condition|(
name|dv
operator|!=
literal|null
condition|)
block|{
name|dvSource
operator|=
name|diskResident
condition|?
name|dv
operator|.
name|getDirectSource
argument_list|()
else|:
name|dv
operator|.
name|getSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dvSource
operator|=
name|getDefaultSource
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
name|setDocValuesSources
argument_list|(
name|dvSource
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the idv source for concrete implementations to use.    *    * @param source The idv source to be used by concrete implementations    */
DECL|method|setDocValuesSources
specifier|protected
specifier|abstract
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|)
function_decl|;
comment|/**    * @return The default source when no doc values are available.    * @param readerContext The current reader context    */
DECL|method|getDefaultSource
specifier|protected
name|DocValues
operator|.
name|Source
name|getDefaultSource
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|getDefaultSource
argument_list|(
name|valueType
argument_list|)
return|;
block|}
comment|// A general impl that works for any group sort.
DECL|class|GeneralAllGroupHeadsCollector
specifier|static
specifier|abstract
class|class
name|GeneralAllGroupHeadsCollector
extends|extends
name|DVAllGroupHeadsCollector
argument_list|<
name|DVAllGroupHeadsCollector
operator|.
name|GroupHead
argument_list|>
block|{
DECL|field|sortWithinGroup
specifier|private
specifier|final
name|Sort
name|sortWithinGroup
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Map
argument_list|<
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|,
name|GroupHead
argument_list|>
name|groups
decl_stmt|;
DECL|method|GeneralAllGroupHeadsCollector
name|GeneralAllGroupHeadsCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|sortWithinGroup
operator|.
name|getSort
argument_list|()
operator|.
name|length
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
name|this
operator|.
name|sortWithinGroup
operator|=
name|sortWithinGroup
expr_stmt|;
name|groups
operator|=
operator|new
name|HashMap
argument_list|<
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|,
name|GroupHead
argument_list|>
argument_list|()
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sortWithinGroup
operator|.
name|getSort
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reversed
index|[
name|i
index|]
operator|=
name|sortFields
index|[
name|i
index|]
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
DECL|method|retrieveGroupHeadAndAddIfNotExist
specifier|protected
name|void
name|retrieveGroupHeadAndAddIfNotExist
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Comparable
argument_list|<
name|?
argument_list|>
name|groupValue
init|=
name|getGroupValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|GroupHead
name|groupHead
init|=
name|groups
operator|.
name|get
argument_list|(
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupHead
operator|==
literal|null
condition|)
block|{
name|groupHead
operator|=
operator|new
name|GroupHead
argument_list|(
name|groupValue
argument_list|,
name|sortWithinGroup
argument_list|,
name|doc
argument_list|,
name|readerContext
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|groupValue
operator|==
literal|null
condition|?
literal|null
else|:
name|duplicate
argument_list|(
name|groupValue
argument_list|)
argument_list|,
name|groupHead
argument_list|)
expr_stmt|;
name|temporalResult
operator|.
name|stop
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|temporalResult
operator|.
name|stop
operator|=
literal|false
expr_stmt|;
block|}
name|temporalResult
operator|.
name|groupHead
operator|=
name|groupHead
expr_stmt|;
block|}
DECL|method|getGroupValue
specifier|protected
specifier|abstract
name|Comparable
argument_list|<
name|?
argument_list|>
name|getGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
DECL|method|duplicate
specifier|protected
specifier|abstract
name|Comparable
argument_list|<
name|?
argument_list|>
name|duplicate
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
function_decl|;
DECL|method|getCollectedGroupHeads
specifier|protected
name|Collection
argument_list|<
name|GroupHead
argument_list|>
name|getCollectedGroupHeads
parameter_list|()
block|{
return|return
name|groups
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
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
name|groupHead
operator|.
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|groupHead
operator|.
name|comparators
index|[
name|i
index|]
operator|=
name|groupHead
operator|.
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|groupHead
operator|.
name|readerContext
operator|=
name|context
expr_stmt|;
block|}
block|}
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
name|groupHead
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|comparator
range|:
name|groupHead
operator|.
name|comparators
control|)
block|{
name|comparator
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|SortedBR
specifier|static
class|class
name|SortedBR
extends|extends
name|GeneralAllGroupHeadsCollector
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|SortedSource
name|source
decl_stmt|;
DECL|method|SortedBR
name|SortedBR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
block|}
DECL|method|getGroupValue
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|getGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|scratchBytesRef
argument_list|)
return|;
block|}
DECL|method|duplicate
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|duplicate
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
operator|.
name|asSortedSource
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultSource
specifier|protected
name|DocValues
operator|.
name|Source
name|getDefaultSource
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|getDefaultSortedSource
argument_list|(
name|valueType
argument_list|,
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|BR
specifier|static
class|class
name|BR
extends|extends
name|GeneralAllGroupHeadsCollector
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|method|BR
name|BR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
block|}
DECL|method|getGroupValue
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|getGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|scratchBytesRef
argument_list|)
return|;
block|}
DECL|method|duplicate
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|duplicate
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|Lng
specifier|static
class|class
name|Lng
extends|extends
name|GeneralAllGroupHeadsCollector
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|method|Lng
name|Lng
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
block|}
DECL|method|getGroupValue
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|getGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getInt
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|duplicate
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|duplicate
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|Dbl
specifier|static
class|class
name|Dbl
extends|extends
name|GeneralAllGroupHeadsCollector
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|method|Dbl
name|Dbl
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|boolean
name|diskResident
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|sortWithinGroup
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
block|}
DECL|method|getGroupValue
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|getGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|duplicate
specifier|protected
name|Comparable
argument_list|<
name|?
argument_list|>
name|duplicate
parameter_list|(
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

