begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|Arrays
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
name|Collection
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
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
operator|.
name|*
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
name|SolrInputDocument
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
name|SolrInputField
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
name|SolrException
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
name|core
operator|.
name|SolrResourceLoader
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
name|IndexSchema
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
name|FieldType
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Reusable base class for UpdateProcessors that will consider   * AddUpdateCommands and mutate the values associated with configured  * fields.  *<p>  * Subclasses should override the mutate method to specify how individual   * SolrInputFields identified by the selector associated with this instance   * will be mutated.  *</p>  *  * @see FieldMutatingUpdateProcessorFactory  * @see FieldValueMutatingUpdateProcessor  * @see FieldNameSelector  */
end_comment

begin_class
DECL|class|FieldMutatingUpdateProcessor
specifier|public
specifier|abstract
class|class
name|FieldMutatingUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldMutatingUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|selector
specifier|private
specifier|final
name|FieldNameSelector
name|selector
decl_stmt|;
DECL|method|FieldMutatingUpdateProcessor
specifier|public
name|FieldMutatingUpdateProcessor
parameter_list|(
name|FieldNameSelector
name|selector
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
comment|/**    * Method for mutating SolrInputFields associated with fields identified     * by the FieldNameSelector associated with this processor    * @param src the SolrInputField to mutate, may be modified in place and     *            returned    * @return the SolrInputField to use in replacing the original (src) value.    *         If null the field will be removed.    */
DECL|method|mutate
specifier|protected
specifier|abstract
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|src
parameter_list|)
function_decl|;
comment|/**    * Calls<code>mutate</code> on any fields identified by the selector     * before forwarding the command down the chain.  Any SolrExceptions     * thrown by<code>mutate</code> will be logged with the Field name,     * wrapped and re-thrown.    */
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SolrInputDocument
name|doc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
comment|// make a copy we can iterate over while mutating the doc
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|doc
operator|.
name|getFieldNames
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fname
range|:
name|fieldNames
control|)
block|{
if|if
condition|(
operator|!
name|selector
operator|.
name|shouldMutate
argument_list|(
name|fname
argument_list|)
condition|)
continue|continue;
specifier|final
name|SolrInputField
name|src
init|=
name|doc
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|SolrInputField
name|dest
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dest
operator|=
name|mutate
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unable to mutate field '"
operator|+
name|fname
operator|+
literal|"': "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|dest
condition|)
block|{
name|doc
operator|.
name|remove
argument_list|(
name|fname
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// semantics of what happens if dest has diff name are hard
comment|// we could treat it as a copy, or a rename
comment|// for now, don't allow it.
if|if
condition|(
operator|!
name|fname
operator|.
name|equals
argument_list|(
name|dest
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"mutute returned field with different name: "
operator|+
name|fname
operator|+
literal|" => "
operator|+
name|dest
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|doc
operator|.
name|put
argument_list|(
name|dest
operator|.
name|getName
argument_list|()
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Interface for idenfifying which fileds should be mutated    */
DECL|interface|FieldNameSelector
specifier|public
specifier|static
interface|interface
name|FieldNameSelector
block|{
DECL|method|shouldMutate
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
comment|/** Singleton indicating all fields should be mutated */
DECL|field|SELECT_ALL_FIELDS
specifier|public
specifier|static
specifier|final
name|FieldNameSelector
name|SELECT_ALL_FIELDS
init|=
operator|new
name|FieldNameSelector
argument_list|()
block|{
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/** Singleton indicating no fields should be mutated */
DECL|field|SELECT_NO_FIELDS
specifier|public
specifier|static
specifier|final
name|FieldNameSelector
name|SELECT_NO_FIELDS
init|=
operator|new
name|FieldNameSelector
argument_list|()
block|{
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
comment|/**     * Wraps two FieldNameSelectors such that the FieldNameSelector     * returned matches all fields specified by the "includes" unless they     * are matched by "excludes"    * @param includes a selector identifying field names that should be selected    * @param excludes a selector identifying field names that should be     *<i>not</i> be selected, even if they are matched by the 'includes'     *        selector    * @return Either a new FieldNameSelector or one of the input selecors     *         if the combination lends itself to optimization.    */
DECL|method|wrap
specifier|public
specifier|static
name|FieldNameSelector
name|wrap
parameter_list|(
specifier|final
name|FieldNameSelector
name|includes
parameter_list|,
specifier|final
name|FieldNameSelector
name|excludes
parameter_list|)
block|{
if|if
condition|(
name|SELECT_NO_FIELDS
operator|==
name|excludes
condition|)
block|{
return|return
name|includes
return|;
block|}
if|if
condition|(
name|SELECT_ALL_FIELDS
operator|==
name|excludes
condition|)
block|{
return|return
name|SELECT_NO_FIELDS
return|;
block|}
if|if
condition|(
name|SELECT_ALL_FIELDS
operator|==
name|includes
condition|)
block|{
return|return
operator|new
name|FieldNameSelector
argument_list|()
block|{
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|!
name|excludes
operator|.
name|shouldMutate
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
return|;
block|}
return|return
operator|new
name|FieldNameSelector
argument_list|()
block|{
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|includes
operator|.
name|shouldMutate
argument_list|(
name|fieldName
argument_list|)
operator|&&
operator|!
name|excludes
operator|.
name|shouldMutate
argument_list|(
name|fieldName
argument_list|)
operator|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Utility method that can be used to define a FieldNameSelector    * using the same types of rules as the FieldMutatingUpdateProcessor init     * code.  This may be useful for Factories that wish to define default     * selectors in similar terms to what the configuration would look like.    * @lucene.internal    */
DECL|method|createFieldNameSelector
specifier|public
specifier|static
name|FieldNameSelector
name|createFieldNameSelector
parameter_list|(
specifier|final
name|SolrResourceLoader
name|loader
parameter_list|,
specifier|final
name|IndexSchema
name|schema
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|typeNames
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|typeClasses
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|regexes
parameter_list|,
specifier|final
name|FieldNameSelector
name|defSelector
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|(
name|typeClasses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|typeClasses
control|)
block|{
try|try
block|{
name|classes
operator|.
name|add
argument_list|(
name|loader
operator|.
name|findClass
argument_list|(
name|t
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Can't resolve typeClass: "
operator|+
name|t
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|classes
operator|.
name|isEmpty
argument_list|()
operator|&&
name|typeNames
operator|.
name|isEmpty
argument_list|()
operator|&&
name|regexes
operator|.
name|isEmpty
argument_list|()
operator|&&
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defSelector
return|;
block|}
return|return
operator|new
name|ConfigurableFieldNameSelector
argument_list|(
name|schema
argument_list|,
name|fields
argument_list|,
name|typeNames
argument_list|,
name|classes
argument_list|,
name|regexes
argument_list|)
return|;
block|}
DECL|class|ConfigurableFieldNameSelector
specifier|private
specifier|static
specifier|final
class|class
name|ConfigurableFieldNameSelector
implements|implements
name|FieldNameSelector
block|{
DECL|field|schema
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|fields
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|typeNames
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|typeNames
decl_stmt|;
DECL|field|classes
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|>
name|classes
decl_stmt|;
DECL|field|regexes
specifier|final
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|regexes
decl_stmt|;
DECL|method|ConfigurableFieldNameSelector
specifier|private
name|ConfigurableFieldNameSelector
parameter_list|(
specifier|final
name|IndexSchema
name|schema
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|typeNames
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|>
name|classes
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|regexes
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|typeNames
operator|=
name|typeNames
expr_stmt|;
name|this
operator|.
name|classes
operator|=
name|classes
expr_stmt|;
name|this
operator|.
name|regexes
operator|=
name|regexes
expr_stmt|;
block|}
DECL|method|shouldMutate
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
comment|// order of checks is bsaed on what should be quicker
comment|// (ie: set lookups faster the looping over instanceOf / matches tests
if|if
condition|(
operator|!
operator|(
name|fields
operator|.
name|isEmpty
argument_list|()
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// do not consider it an error if the fieldName has no type
comment|// there might be another processor dealing with it later
name|FieldType
name|t
init|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|t
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|typeNames
operator|.
name|isEmpty
argument_list|()
operator|||
name|typeNames
operator|.
name|contains
argument_list|(
name|t
operator|.
name|getTypeName
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|classes
operator|.
name|isEmpty
argument_list|()
operator|||
name|instanceOfAny
argument_list|(
name|t
argument_list|,
name|classes
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|regexes
operator|.
name|isEmpty
argument_list|()
operator|||
name|matchesAny
argument_list|(
name|fieldName
argument_list|,
name|regexes
argument_list|)
operator|)
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
comment|/**      * returns true if the Object 'o' is an instance of any class in       * the Collection      */
DECL|method|instanceOfAny
specifier|private
specifier|static
name|boolean
name|instanceOfAny
parameter_list|(
name|Object
name|o
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|>
name|classes
parameter_list|)
block|{
for|for
control|(
name|Class
name|c
range|:
name|classes
control|)
block|{
if|if
condition|(
name|c
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * returns true if the CharSequence 's' matches any Pattern in the       * Collection      */
DECL|method|matchesAny
specifier|private
specifier|static
name|boolean
name|matchesAny
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|regexes
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|p
range|:
name|regexes
control|)
block|{
if|if
condition|(
name|p
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

