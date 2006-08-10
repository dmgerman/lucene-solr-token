begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|config
package|;
end_package

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
name|HashSet
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Analyzer
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
name|PerFieldAnalyzerWrapper
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
name|standard
operator|.
name|StandardAnalyzer
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
name|gdata
operator|.
name|search
operator|.
name|index
operator|.
name|IndexDocument
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
name|gdata
operator|.
name|utils
operator|.
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * This class is used to configure the indexing and search component. Each  * service on the GData server will have an own search index. For this purpose  * one single index schema will be configured in the gdata-config.xml file. This  * file will be mapped on this class on startup.  *<p>  * This class breaks some encapsulation of general java classes to be  * configurable via the xml configuration file. The will be very less type and  * value checking of the properties inside this file. Mandatory values must be  * set in the configuration file. The server won't start up if these values are  * missing. See definition in the xml schema file. If this class is instantiated  * manually the value for the name of the schema should be set before this is  * passed to the IndexController.  *</p>  *<p>  * One IndexSchema consists of multiple instances of  * {@link org.apache.lucene.gdata.search.config.IndexSchemaField} each of this  * instances describes a single field in the index and all schema informations  * about the field.  *<p>  *   *   * @see org.apache.lucene.gdata.search.config.IndexSchemaField  *   *   * @author Simon Willnauer  */
end_comment

begin_class
DECL|class|IndexSchema
specifier|public
class|class
name|IndexSchema
block|{
DECL|field|searchableFieldNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|searchableFieldNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IndexSchema
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * a static final value for properties are not set by the configuration file      * this value will be set to all long and int properties by default      */
DECL|field|NOT_SET_VALUE
specifier|public
specifier|static
specifier|final
name|int
name|NOT_SET_VALUE
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT_OPTIMIZE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_OPTIMIZE_COUNT
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_COMMIT_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_COMMIT_COUNT
init|=
literal|1
decl_stmt|;
DECL|field|indexLocation
specifier|private
name|String
name|indexLocation
decl_stmt|;
comment|/*      * this should be final change it if possible --> see commons digester /      * RegistryBuilder      */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|useTimedIndexer
specifier|private
name|boolean
name|useTimedIndexer
decl_stmt|;
DECL|field|indexerIdleTime
specifier|private
name|long
name|indexerIdleTime
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|serviceAnalyzer
specifier|private
name|Analyzer
name|serviceAnalyzer
decl_stmt|;
DECL|field|defaultSearchField
specifier|private
name|String
name|defaultSearchField
decl_stmt|;
DECL|field|perFieldAnalyzer
specifier|private
name|PerFieldAnalyzerWrapper
name|perFieldAnalyzer
decl_stmt|;
DECL|field|schemaFields
specifier|private
name|Collection
argument_list|<
name|IndexSchemaField
argument_list|>
name|schemaFields
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|private
name|int
name|maxBufferedDocs
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|maxMergeDocs
specifier|private
name|int
name|maxMergeDocs
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|mergeFactor
specifier|private
name|int
name|mergeFactor
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|maxFieldLength
specifier|private
name|int
name|maxFieldLength
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|writeLockTimeout
specifier|private
name|long
name|writeLockTimeout
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|commitLockTimeout
specifier|private
name|long
name|commitLockTimeout
init|=
name|NOT_SET_VALUE
decl_stmt|;
DECL|field|commitAfterDocuments
specifier|private
name|int
name|commitAfterDocuments
init|=
name|DEFAULT_COMMIT_COUNT
decl_stmt|;
DECL|field|optimizeAfterCommit
specifier|private
name|int
name|optimizeAfterCommit
init|=
name|DEFAULT_OPTIMIZE_COUNT
decl_stmt|;
DECL|field|useCompoundFile
specifier|private
name|boolean
name|useCompoundFile
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a new IndexSchema and initialize the standard service analyzer to      * {@link StandardAnalyzer}      *       */
DECL|method|IndexSchema
specifier|public
name|IndexSchema
parameter_list|()
block|{
name|this
operator|.
name|schemaFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|IndexSchemaField
argument_list|>
argument_list|()
expr_stmt|;
comment|/*          * keep as standard if omitted in the configuration          */
name|this
operator|.
name|serviceAnalyzer
operator|=
operator|new
name|StandardAnalyzer
argument_list|()
expr_stmt|;
block|}
comment|/**      * Initialize the schema and checks all required values      */
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{
for|for
control|(
name|IndexSchemaField
name|field
range|:
name|this
operator|.
name|schemaFields
control|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|checkRequieredValues
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Required Value for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|defaultSearchField
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"DefaulSearchField must not be null"
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Schema field is not set -- must not be null"
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|indexLocation
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IndexLocation must not be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|this
operator|.
name|searchableFieldNames
operator|.
name|contains
argument_list|(
name|this
operator|.
name|defaultSearchField
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"the default search field: "
operator|+
name|this
operator|.
name|defaultSearchField
operator|+
literal|" is registered as a field"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return Returns the useCompoundFile.      */
DECL|method|isUseCompoundFile
specifier|public
name|boolean
name|isUseCompoundFile
parameter_list|()
block|{
return|return
name|this
operator|.
name|useCompoundFile
return|;
block|}
comment|/**      * @param useCompoundFile      *            The useCompoundFile to set.      */
DECL|method|setUseCompoundFile
specifier|public
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
comment|/**      * Adds a new {@link IndexSchemaField} to the schema. if the fields name      * equals {@link IndexDocument#FIELD_ENTRY_ID} or the field is      *<code>null</code> it will simply ignored      *       * @param field -      *            the index schema field to add as a field of this schema.      */
DECL|method|addSchemaField
specifier|public
name|void
name|addSchemaField
parameter_list|(
specifier|final
name|IndexSchemaField
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
return|return;
comment|/*          * skip fields configured in the gdata-config.xml file if their names          * match a primary key field id of the IndexDocument          */
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|||
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|IndexDocument
operator|.
name|FIELD_FEED_ID
argument_list|)
condition|)
return|return;
if|if
condition|(
name|field
operator|.
name|getAnalyzerClass
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|/*              * enable per field analyzer if one is set.              */
name|Analyzer
name|analyzer
init|=
name|getAnalyzerInstance
argument_list|(
name|field
operator|.
name|getAnalyzerClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/*              * null values will be omitted here              */
name|buildPerFieldAnalyzerWrapper
argument_list|(
name|analyzer
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|schemaFields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchableFieldNames
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the fieldConfiguration.      */
DECL|method|getFields
specifier|public
name|Collection
argument_list|<
name|IndexSchemaField
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|this
operator|.
name|schemaFields
return|;
block|}
comment|/**      * @return - the analyzer instance to be used for this schema      */
DECL|method|getSchemaAnalyzer
specifier|public
name|Analyzer
name|getSchemaAnalyzer
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|perFieldAnalyzer
operator|==
literal|null
condition|)
return|return
name|this
operator|.
name|serviceAnalyzer
return|;
return|return
name|this
operator|.
name|perFieldAnalyzer
return|;
block|}
comment|/**      * @return Returns the serviceAnalyzer.      */
DECL|method|getServiceAnalyzer
specifier|public
name|Analyzer
name|getServiceAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceAnalyzer
return|;
block|}
comment|/**      * @param serviceAnalyzer      *            The serviceAnalyzer to set.      */
DECL|method|setServiceAnalyzer
specifier|public
name|void
name|setServiceAnalyzer
parameter_list|(
name|Analyzer
name|serviceAnalyzer
parameter_list|)
block|{
if|if
condition|(
name|serviceAnalyzer
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|serviceAnalyzer
operator|=
name|serviceAnalyzer
expr_stmt|;
block|}
comment|/**      * @return Returns the commitLockTimout.      */
DECL|method|getCommitLockTimeout
specifier|public
name|long
name|getCommitLockTimeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitLockTimeout
return|;
block|}
comment|/**      *       * @param commitLockTimeout      *            The commitLockTimeout to set.      */
DECL|method|setCommitLockTimeout
specifier|public
name|void
name|setCommitLockTimeout
parameter_list|(
name|long
name|commitLockTimeout
parameter_list|)
block|{
comment|// TODO enable this in config
name|this
operator|.
name|commitLockTimeout
operator|=
name|commitLockTimeout
expr_stmt|;
block|}
comment|/**      * @return Returns the maxBufferedDocs.      */
DECL|method|getMaxBufferedDocs
specifier|public
name|int
name|getMaxBufferedDocs
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxBufferedDocs
return|;
block|}
comment|/**      * @param maxBufferedDocs      *            The maxBufferedDocs to set.      */
DECL|method|setMaxBufferedDocs
specifier|public
name|void
name|setMaxBufferedDocs
parameter_list|(
name|int
name|maxBufferedDocs
parameter_list|)
block|{
name|this
operator|.
name|maxBufferedDocs
operator|=
name|maxBufferedDocs
expr_stmt|;
block|}
comment|/**      * @return Returns the maxFieldLength.      */
DECL|method|getMaxFieldLength
specifier|public
name|int
name|getMaxFieldLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxFieldLength
return|;
block|}
comment|/**      * @param maxFieldLength      *            The maxFieldLength to set.      */
DECL|method|setMaxFieldLength
specifier|public
name|void
name|setMaxFieldLength
parameter_list|(
name|int
name|maxFieldLength
parameter_list|)
block|{
name|this
operator|.
name|maxFieldLength
operator|=
name|maxFieldLength
expr_stmt|;
block|}
comment|/**      * @return Returns the maxMergeDocs.      */
DECL|method|getMaxMergeDocs
specifier|public
name|int
name|getMaxMergeDocs
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxMergeDocs
return|;
block|}
comment|/**      * @param maxMergeDocs      *            The maxMergeDocs to set.      */
DECL|method|setMaxMergeDocs
specifier|public
name|void
name|setMaxMergeDocs
parameter_list|(
name|int
name|maxMergeDocs
parameter_list|)
block|{
name|this
operator|.
name|maxMergeDocs
operator|=
name|maxMergeDocs
expr_stmt|;
block|}
comment|/**      * @return Returns the mergeFactor.      */
DECL|method|getMergeFactor
specifier|public
name|int
name|getMergeFactor
parameter_list|()
block|{
return|return
name|this
operator|.
name|mergeFactor
return|;
block|}
comment|/**      * @param mergeFactor      *            The mergeFactor to set.      */
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
block|}
comment|/**      * @return Returns the writeLockTimeout.      */
DECL|method|getWriteLockTimeout
specifier|public
name|long
name|getWriteLockTimeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|writeLockTimeout
return|;
block|}
comment|/**      * @param writeLockTimeout      *            The writeLockTimeout to set.      */
DECL|method|setWriteLockTimeout
specifier|public
name|void
name|setWriteLockTimeout
parameter_list|(
name|long
name|writeLockTimeout
parameter_list|)
block|{
name|this
operator|.
name|writeLockTimeout
operator|=
name|writeLockTimeout
expr_stmt|;
block|}
comment|/**      * @param fields      *            The fieldConfiguration to set.      */
DECL|method|setSchemaFields
specifier|public
name|void
name|setSchemaFields
parameter_list|(
name|Collection
argument_list|<
name|IndexSchemaField
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|schemaFields
operator|=
name|fields
expr_stmt|;
block|}
comment|/**      * @return Returns the name.      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|object
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|object
operator|instanceof
name|IndexSchema
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|name
operator|==
literal|null
condition|)
return|return
name|super
operator|.
name|equals
argument_list|(
name|object
argument_list|)
return|;
return|return
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|IndexSchema
operator|)
name|object
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @see java.lang.Object#hashCode()      */
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
name|this
operator|.
name|name
operator|==
literal|null
condition|)
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
return|return
name|this
operator|.
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|buildPerFieldAnalyzerWrapper
specifier|private
name|void
name|buildPerFieldAnalyzerWrapper
parameter_list|(
name|Analyzer
name|anazlyer
parameter_list|,
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|anazlyer
operator|==
literal|null
operator|||
name|field
operator|==
literal|null
operator|||
name|field
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|this
operator|.
name|perFieldAnalyzer
operator|==
literal|null
condition|)
name|this
operator|.
name|perFieldAnalyzer
operator|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|this
operator|.
name|serviceAnalyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|perFieldAnalyzer
operator|.
name|addAnalyzer
argument_list|(
name|field
argument_list|,
name|anazlyer
argument_list|)
expr_stmt|;
block|}
DECL|method|getAnalyzerInstance
specifier|private
specifier|static
name|Analyzer
name|getAnalyzerInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ReflectionUtils
operator|.
name|extendsType
argument_list|(
name|clazz
argument_list|,
name|Analyzer
operator|.
name|class
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can not create analyzer for class "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can not create analyzer for class "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @param name      *            The name to set.      */
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * @return Returns the indexLocation.      */
DECL|method|getIndexLocation
specifier|public
name|String
name|getIndexLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexLocation
return|;
block|}
comment|/**      * @param indexLocation      *            The indexLocation to set.      */
DECL|method|setIndexLocation
specifier|public
name|void
name|setIndexLocation
parameter_list|(
name|String
name|indexLocation
parameter_list|)
block|{
name|this
operator|.
name|indexLocation
operator|=
name|indexLocation
expr_stmt|;
block|}
comment|/**      * @return Returns the defaultField.      */
DECL|method|getDefaultSearchField
specifier|public
name|String
name|getDefaultSearchField
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultSearchField
return|;
block|}
comment|/**      * @param defaultField      *            The defaultField to set.      */
DECL|method|setDefaultSearchField
specifier|public
name|void
name|setDefaultSearchField
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|this
operator|.
name|defaultSearchField
operator|=
name|defaultField
expr_stmt|;
block|}
comment|/**      * @return Returns the indexerIdleTime.      */
DECL|method|getIndexerIdleTime
specifier|public
name|long
name|getIndexerIdleTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexerIdleTime
return|;
block|}
comment|/**      * @param indexerIdleTime      *            The indexerIdleTime to set.      */
DECL|method|setIndexerIdleTime
specifier|public
name|void
name|setIndexerIdleTime
parameter_list|(
name|long
name|indexerIdleTime
parameter_list|)
block|{
name|this
operator|.
name|indexerIdleTime
operator|=
name|indexerIdleTime
expr_stmt|;
block|}
comment|/**      * @return Returns the useTimedIndexer.      */
DECL|method|isUseTimedIndexer
specifier|public
name|boolean
name|isUseTimedIndexer
parameter_list|()
block|{
return|return
name|this
operator|.
name|useTimedIndexer
return|;
block|}
comment|/**      * @param useTimedIndexer      *            The useTimedIndexer to set.      */
DECL|method|setUseTimedIndexer
specifier|public
name|void
name|setUseTimedIndexer
parameter_list|(
name|boolean
name|useTimedIndexer
parameter_list|)
block|{
name|this
operator|.
name|useTimedIndexer
operator|=
name|useTimedIndexer
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Name: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"MaxBufferedDocs: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|maxBufferedDocs
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"MaxFieldLength: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|maxFieldLength
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"MaxMergeDocs: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|maxMergeDocs
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"MergeFactor: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|mergeFactor
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"CommitLockTimeout: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|commitLockTimeout
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"WriteLockTimeout: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|writeLockTimeout
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"indexerIdleTime: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|indexerIdleTime
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"useCompoundFile: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|useCompoundFile
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Added SchemaField instances: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|schemaFields
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"IndexLocation: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|indexLocation
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * @return Returns the searchableFieldNames.      */
DECL|method|getSearchableFieldNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSearchableFieldNames
parameter_list|()
block|{
return|return
name|this
operator|.
name|searchableFieldNames
return|;
block|}
comment|/**      * Defines after how many added,removed or updated document the indexer should commit.      * @return Returns the commitAfterDocuments.      */
DECL|method|getCommitAfterDocuments
specifier|public
name|int
name|getCommitAfterDocuments
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitAfterDocuments
return|;
block|}
comment|/**      * @param commitAfterDocuments The commitAfterDocuments to set.      */
DECL|method|setCommitAfterDocuments
specifier|public
name|void
name|setCommitAfterDocuments
parameter_list|(
name|int
name|commitAfterDocuments
parameter_list|)
block|{
if|if
condition|(
name|commitAfterDocuments
operator|<
name|DEFAULT_COMMIT_COUNT
condition|)
return|return;
name|this
operator|.
name|commitAfterDocuments
operator|=
name|commitAfterDocuments
expr_stmt|;
block|}
comment|/**      * Defines after how many commits the indexer should optimize the index      * @return Returns the optimizeAfterCommit.      */
DECL|method|getOptimizeAfterCommit
specifier|public
name|int
name|getOptimizeAfterCommit
parameter_list|()
block|{
return|return
name|this
operator|.
name|optimizeAfterCommit
return|;
block|}
comment|/**      * @param optimizeAfterCommit The optimizeAfterCommit to set.      */
DECL|method|setOptimizeAfterCommit
specifier|public
name|void
name|setOptimizeAfterCommit
parameter_list|(
name|int
name|optimizeAfterCommit
parameter_list|)
block|{
if|if
condition|(
name|optimizeAfterCommit
operator|<
name|DEFAULT_OPTIMIZE_COUNT
condition|)
return|return;
name|this
operator|.
name|optimizeAfterCommit
operator|=
name|optimizeAfterCommit
expr_stmt|;
block|}
block|}
end_class

end_unit

