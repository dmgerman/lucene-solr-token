begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|SchemaAware
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|FieldType
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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

begin_comment
comment|/**  * A factory interface for configuring a {@link Similarity} in the Solr   * schema.xml.    *   *<p>  * Subclasses of<code>SimilarityFactory</code> which are {@link SchemaAware}   * must take responsibility for either consulting the similarities configured   * on individual field types, or generating appropriate error/warning messages   * if field type specific similarities exist but are being ignored.  The   *<code>IndexSchema</code> will provide such error checking if a   * non-<code>SchemaAware</code> instance of<code>SimilarityFactory</code>   * is used.  *   * @see FieldType#getSimilarity  */
end_comment

begin_class
DECL|class|SimilarityFactory
specifier|public
specifier|abstract
class|class
name|SimilarityFactory
block|{
DECL|field|CLASS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CLASS_NAME
init|=
literal|"class"
decl_stmt|;
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|getSimilarity
specifier|public
specifier|abstract
name|Similarity
name|getSimilarity
parameter_list|()
function_decl|;
DECL|method|normalizeSPIname
specifier|private
specifier|static
name|String
name|normalizeSPIname
parameter_list|(
name|String
name|fullyQualifiedName
parameter_list|)
block|{
if|if
condition|(
name|fullyQualifiedName
operator|.
name|startsWith
argument_list|(
literal|"org.apache.lucene."
argument_list|)
operator|||
name|fullyQualifiedName
operator|.
name|startsWith
argument_list|(
literal|"org.apache.solr."
argument_list|)
condition|)
block|{
return|return
literal|"solr"
operator|+
name|fullyQualifiedName
operator|.
name|substring
argument_list|(
name|fullyQualifiedName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
return|;
block|}
return|return
name|fullyQualifiedName
return|;
block|}
comment|/** Returns a description of this field's similarity, if any */
DECL|method|getNamedPropertyValues
specifier|public
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getNamedPropertyValues
parameter_list|()
block|{
name|String
name|className
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|className
operator|.
name|startsWith
argument_list|(
literal|"org.apache.solr.schema.IndexSchema$"
argument_list|)
condition|)
block|{
comment|// If this class is just a no-params wrapper around a similarity class, use the similarity class
name|className
operator|=
name|getSimilarity
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Only normalize factory names
name|className
operator|=
name|normalizeSPIname
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|props
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|add
argument_list|(
name|CLASS_NAME
argument_list|,
name|normalizeSPIname
argument_list|(
name|className
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|params
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|props
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
block|}
end_class

end_unit

