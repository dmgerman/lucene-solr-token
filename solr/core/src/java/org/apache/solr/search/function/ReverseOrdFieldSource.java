begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|IndexReader
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
name|LeafReader
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
name|LeafReaderContext
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
name|MultiReader
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
name|index
operator|.
name|SortedDocValues
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|IntDocValues
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
name|SortedSetSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|index
operator|.
name|SlowCompositeReaderWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|Insanity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * Obtains the ordinal of the field value from {@link org.apache.lucene.index.LeafReader#getSortedDocValues}  * and reverses the order.  *<br>  * The native lucene index order is used to assign an ordinal value for each field value.  *<br>Field values (terms) are lexicographically ordered by unicode value, and numbered starting at 1.  *<br>  * Example of reverse ordinal (rord):<br>  *  If there were only three field values: "apple","banana","pear"  *<br>then rord("apple")=3, rord("banana")=2, ord("pear")=1  *<p>  *  WARNING: ord() depends on the position in an index and can thus change when other documents are inserted or deleted,  *  or if a MultiSearcher is used.  *<br>  *  WARNING: as of Solr 1.4, ord() and rord() can cause excess memory use since they must use a FieldCache entry  * at the top level reader, while sorting and function queries now use entries at the segment level.  Hence sorting  * or using a different function query, in addition to ord()/rord() will double memory use.  *   *  */
end_comment

begin_class
DECL|class|ReverseOrdFieldSource
specifier|public
class|class
name|ReverseOrdFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|public
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|ReverseOrdFieldSource
specifier|public
name|ReverseOrdFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"rord("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|off
init|=
name|readerContext
operator|.
name|docBase
decl_stmt|;
specifier|final
name|LeafReader
name|r
decl_stmt|;
name|Object
name|o
init|=
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|SolrIndexSearcher
condition|)
block|{
name|SolrIndexSearcher
name|is
init|=
operator|(
name|SolrIndexSearcher
operator|)
name|o
decl_stmt|;
name|SchemaField
name|sf
init|=
name|is
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
name|sf
operator|.
name|hasDocValues
argument_list|()
operator|==
literal|false
operator|&&
name|sf
operator|.
name|multiValued
argument_list|()
operator|==
literal|false
operator|&&
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// it's a single-valued numeric field: we must currently create insanity :(
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|is
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|LeafReader
name|insaneLeaves
index|[]
init|=
operator|new
name|LeafReader
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|raw
range|:
name|leaves
control|)
block|{
name|insaneLeaves
index|[
name|upto
operator|++
index|]
operator|=
name|Insanity
operator|.
name|wrapInsanity
argument_list|(
name|raw
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|MultiReader
argument_list|(
name|insaneLeaves
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// reuse ordinalmap
name|r
operator|=
operator|(
operator|(
name|SolrIndexSearcher
operator|)
name|o
operator|)
operator|.
name|getSlowAtomicReader
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|IndexReader
name|topReader
init|=
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|r
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|topReader
argument_list|)
expr_stmt|;
block|}
comment|// if it's e.g. tokenized/multivalued, emulate old behavior of single-valued fc
specifier|final
name|SortedDocValues
name|sindex
init|=
name|SortedSetSelector
operator|.
name|wrap
argument_list|(
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
argument_list|,
name|SortedSetSelector
operator|.
name|Type
operator|.
name|MIN
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|sindex
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
return|return
operator|new
name|IntDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doc
operator|+
name|off
operator|>
name|sindex
operator|.
name|docID
argument_list|()
condition|)
block|{
name|sindex
operator|.
name|advance
argument_list|(
name|doc
operator|+
name|off
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|+
name|off
operator|==
name|sindex
operator|.
name|docID
argument_list|()
condition|)
block|{
return|return
operator|(
name|end
operator|-
name|sindex
operator|.
name|ordValue
argument_list|()
operator|-
literal|1
operator|)
return|;
block|}
else|else
block|{
return|return
name|end
return|;
block|}
block|}
block|}
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
name|o
operator|==
literal|null
operator|||
operator|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|ReverseOrdFieldSource
operator|.
name|class
operator|)
condition|)
return|return
literal|false
return|;
name|ReverseOrdFieldSource
name|other
init|=
operator|(
name|ReverseOrdFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
specifier|final
name|int
name|hcode
init|=
name|ReverseOrdFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

