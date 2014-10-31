begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|FilterLeafReader
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
name|BinaryDocValues
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
name|DocValuesType
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
name|FieldInfo
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
name|FieldInfos
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
name|NumericDocValues
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
name|index
operator|.
name|SortedSetDocValues
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
name|uninverting
operator|.
name|UninvertingReader
import|;
end_import

begin_comment
comment|/**   * Lucene 5.0 removes "accidental" insanity, so you must explicitly  * create it.  *<p>  * This class creates insanity for two specific situations:  *<ul>  *<li>calling {@code ord} or {@code rord} functions on a single-valued numeric field.  *<li>doing grouped faceting ({@code group.facet}) on a single-valued numeric field.  *</ul>  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Insanity
specifier|public
class|class
name|Insanity
block|{
comment|/**     * Returns a view over {@code sane} where {@code insaneField} is a string    * instead of a numeric.    */
DECL|method|wrapInsanity
specifier|public
specifier|static
name|LeafReader
name|wrapInsanity
parameter_list|(
name|LeafReader
name|sane
parameter_list|,
name|String
name|insaneField
parameter_list|)
block|{
return|return
operator|new
name|UninvertingReader
argument_list|(
operator|new
name|InsaneReader
argument_list|(
name|sane
argument_list|,
name|insaneField
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|insaneField
argument_list|,
name|UninvertingReader
operator|.
name|Type
operator|.
name|SORTED
argument_list|)
argument_list|)
return|;
block|}
comment|/** Hides the proper numeric dv type for the field */
DECL|class|InsaneReader
specifier|private
specifier|static
class|class
name|InsaneReader
extends|extends
name|FilterLeafReader
block|{
DECL|field|insaneField
specifier|final
name|String
name|insaneField
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|method|InsaneReader
name|InsaneReader
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|String
name|insaneField
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|insaneField
operator|=
name|insaneField
expr_stmt|;
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
name|filteredInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|in
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|name
operator|.
name|equals
argument_list|(
name|insaneField
argument_list|)
condition|)
block|{
name|filteredInfos
operator|.
name|add
argument_list|(
operator|new
name|FieldInfo
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|number
argument_list|,
name|fi
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|,
name|fi
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|fi
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
name|DocValuesType
operator|.
name|NONE
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filteredInfos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|filteredInfos
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|filteredInfos
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|insaneField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|insaneField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|insaneField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|insaneField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
comment|// important to override these, so fieldcaches are shared on what we wrap
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

