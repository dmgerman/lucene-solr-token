begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
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
name|analysis
operator|.
name|TokenFilter
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryProperty
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|OrdinalProperty
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
name|facet
operator|.
name|index
operator|.
name|categorypolicy
operator|.
name|OrdinalPolicy
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
name|facet
operator|.
name|index
operator|.
name|categorypolicy
operator|.
name|PathPolicy
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyWriter
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This class adds parents to a {@link CategoryAttributesStream}. The parents  * are added according to the {@link PathPolicy} and {@link OrdinalPolicy} from  * the {@link FacetIndexingParams} given in the constructor.<br>  * By default, category properties are removed when creating parents of a  * certain category. However, it is possible to retain certain property types  * using {@link #addRetainableProperty(Class)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryParentsStream
specifier|public
class|class
name|CategoryParentsStream
extends|extends
name|TokenFilter
block|{
comment|/**    * A {@link TaxonomyWriter} for adding categories and retrieving their    * ordinals.    */
DECL|field|taxonomyWriter
specifier|protected
name|TaxonomyWriter
name|taxonomyWriter
decl_stmt|;
comment|/** An attribute containing all data related to the category */
DECL|field|categoryAttribute
specifier|protected
name|CategoryAttribute
name|categoryAttribute
decl_stmt|;
comment|/** A category property containing the category ordinal */
DECL|field|ordinalProperty
specifier|protected
name|OrdinalProperty
name|ordinalProperty
decl_stmt|;
comment|/**    * A set of property classes that are to be retained when creating a parent    * token.    */
DECL|field|retainableProperties
specifier|private
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|retainableProperties
decl_stmt|;
comment|/** A {@link PathPolicy} for the category's parents' category paths. */
DECL|field|pathPolicy
specifier|private
name|PathPolicy
name|pathPolicy
decl_stmt|;
comment|/** An {@link OrdinalPolicy} for the category's parents' ordinals. */
DECL|field|ordinalPolicy
specifier|private
name|OrdinalPolicy
name|ordinalPolicy
decl_stmt|;
comment|/**    * Constructor.    *     * @param input    *            The input stream to handle, must be derived from    *            {@link CategoryAttributesStream}.    * @param taxonomyWriter    *            The taxonomy writer to use for adding categories and    *            retrieving their ordinals.    * @param indexingParams    *            The indexing params used for filtering parents.    */
DECL|method|CategoryParentsStream
specifier|public
name|CategoryParentsStream
parameter_list|(
name|CategoryAttributesStream
name|input
parameter_list|,
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|categoryAttribute
operator|=
name|this
operator|.
name|addAttribute
argument_list|(
name|CategoryAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|taxonomyWriter
operator|=
name|taxonomyWriter
expr_stmt|;
name|this
operator|.
name|pathPolicy
operator|=
name|indexingParams
operator|.
name|getPathPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|ordinalPolicy
operator|=
name|indexingParams
operator|.
name|getOrdinalPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|ordinalPolicy
operator|.
name|init
argument_list|(
name|taxonomyWriter
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinalProperty
operator|=
operator|new
name|OrdinalProperty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|categoryAttribute
operator|.
name|getCategoryPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// try adding the parent of the current category to the stream
name|clearCategoryProperties
argument_list|()
expr_stmt|;
name|boolean
name|added
init|=
literal|false
decl_stmt|;
comment|// set the parent's ordinal, if illegal set -1
name|int
name|ordinal
init|=
name|this
operator|.
name|ordinalProperty
operator|.
name|getOrdinal
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|!=
operator|-
literal|1
condition|)
block|{
name|ordinal
operator|=
name|this
operator|.
name|taxonomyWriter
operator|.
name|getParent
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|ordinal
argument_list|)
condition|)
block|{
name|this
operator|.
name|ordinalProperty
operator|.
name|setOrdinal
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|categoryAttribute
operator|.
name|addProperty
argument_list|(
name|ordinalProperty
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
throw|;
block|}
name|added
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|ordinalProperty
operator|.
name|setOrdinal
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// set the parent's category path, if illegal set null
name|CategoryPath
name|cp
init|=
name|this
operator|.
name|categoryAttribute
operator|.
name|getCategoryPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|null
condition|)
block|{
name|cp
operator|.
name|trim
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// if ordinal added, must also have category paths
if|if
condition|(
name|added
operator|||
name|this
operator|.
name|pathPolicy
operator|.
name|shouldAdd
argument_list|(
name|cp
argument_list|)
condition|)
block|{
name|this
operator|.
name|categoryAttribute
operator|.
name|setCategoryPath
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|categoryAttribute
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|added
condition|)
block|{
comment|// a legal parent exists
return|return
literal|true
return|;
block|}
block|}
comment|// no more parents - get new category
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|ordinal
init|=
name|taxonomyWriter
operator|.
name|addCategory
argument_list|(
name|this
operator|.
name|categoryAttribute
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|ordinalProperty
operator|.
name|setOrdinal
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|categoryAttribute
operator|.
name|addProperty
argument_list|(
name|this
operator|.
name|ordinalProperty
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Clear the properties of the current {@link CategoryAttribute} attribute    * before setting the parent attributes.<br>    * It is possible to retain properties of certain types the parent tokens,    * using {@link #addRetainableProperty(Class)}.    */
DECL|method|clearCategoryProperties
specifier|protected
name|void
name|clearCategoryProperties
parameter_list|()
block|{
if|if
condition|(
name|retainableProperties
operator|==
literal|null
operator|||
name|retainableProperties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|categoryAttribute
operator|.
name|clearProperties
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|propsToRemove
init|=
operator|new
name|LinkedList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|propertyClass
range|:
name|categoryAttribute
operator|.
name|getPropertyClasses
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|retainableProperties
operator|.
name|contains
argument_list|(
name|propertyClass
argument_list|)
condition|)
block|{
name|propsToRemove
operator|.
name|add
argument_list|(
name|propertyClass
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|propertyClass
range|:
name|propsToRemove
control|)
block|{
name|categoryAttribute
operator|.
name|remove
argument_list|(
name|propertyClass
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add a {@link CategoryProperty} class which is retained when creating    * parent tokens.    *     * @param toRetain    *            The property class to retain.    */
DECL|method|addRetainableProperty
specifier|public
name|void
name|addRetainableProperty
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|toRetain
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|retainableProperties
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|retainableProperties
operator|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|retainableProperties
operator|.
name|add
argument_list|(
name|toRetain
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

