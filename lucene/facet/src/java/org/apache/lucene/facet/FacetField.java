begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Field
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
name|FieldType
import|;
end_import

begin_comment
comment|/**  * Add an instance of this to your {@link Document} for every facet label.  *   *<p>  *<b>NOTE:</b> you must call {@link FacetsConfig#build(Document)} before  * you add the document to IndexWriter.  */
end_comment

begin_class
DECL|class|FacetField
specifier|public
class|class
name|FacetField
extends|extends
name|Field
block|{
DECL|field|TYPE
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Dimension for this field. */
DECL|field|dim
specifier|public
specifier|final
name|String
name|dim
decl_stmt|;
comment|/** Path for this field. */
DECL|field|path
specifier|public
specifier|final
name|String
index|[]
name|path
decl_stmt|;
comment|/** Creates the this from {@code dim} and    *  {@code path}. */
DECL|method|FacetField
specifier|public
name|FacetField
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|super
argument_list|(
literal|"dummy"
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|verifyLabel
argument_list|(
name|dim
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|label
range|:
name|path
control|)
block|{
name|verifyLabel
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dim
operator|=
name|dim
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path must have at least one element"
argument_list|)
throw|;
block|}
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
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
literal|"FacetField(dim="
operator|+
name|dim
operator|+
literal|" path="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|path
argument_list|)
operator|+
literal|")"
return|;
block|}
comment|/** Verifies the label is not null or empty string.    *     *  @lucene.internal */
DECL|method|verifyLabel
specifier|public
specifier|static
name|void
name|verifyLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
operator|||
name|label
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"empty or null components not allowed; got: "
operator|+
name|label
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

