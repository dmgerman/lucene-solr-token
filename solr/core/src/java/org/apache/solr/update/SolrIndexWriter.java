begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|codecs
operator|.
name|Codec
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
name|IndexDeletionPolicy
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|store
operator|.
name|Directory
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
name|InfoStream
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
name|IOUtils
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
name|SuppressForbidden
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
name|DirectoryFactory
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
name|DirectoryFactory
operator|.
name|DirContext
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
name|SolrCore
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
comment|/**  * An IndexWriter that is configured via Solr config mechanisms.  *  * @since solr 0.9  */
end_comment

begin_class
DECL|class|SolrIndexWriter
specifier|public
class|class
name|SolrIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|// These should *only* be used for debugging or monitoring purposes
DECL|field|numOpens
specifier|public
specifier|static
specifier|final
name|AtomicLong
name|numOpens
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numCloses
specifier|public
specifier|static
specifier|final
name|AtomicLong
name|numCloses
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/** Stored into each Lucene commit to record the    *  System.currentTimeMillis() when commit was called. */
DECL|field|COMMIT_TIME_MSEC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_TIME_MSEC_KEY
init|=
literal|"commitTimeMSec"
decl_stmt|;
DECL|field|CLOSE_LOCK
specifier|private
specifier|final
name|Object
name|CLOSE_LOCK
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|directoryFactory
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
DECL|field|infoStream
specifier|private
name|InfoStream
name|infoStream
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|method|create
specifier|public
specifier|static
name|SolrIndexWriter
name|create
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|directoryFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|,
name|Codec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrIndexWriter
name|w
init|=
literal|null
decl_stmt|;
specifier|final
name|Directory
name|d
init|=
name|directoryFactory
operator|.
name|get
argument_list|(
name|path
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|config
operator|.
name|lockType
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|=
operator|new
name|SolrIndexWriter
argument_list|(
name|core
argument_list|,
name|name
argument_list|,
name|path
argument_list|,
name|d
argument_list|,
name|create
argument_list|,
name|schema
argument_list|,
name|config
argument_list|,
name|delPolicy
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|w
operator|.
name|setDirectoryFactory
argument_list|(
name|directoryFactory
argument_list|)
expr_stmt|;
return|return
name|w
return|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|==
name|w
operator|&&
literal|null
operator|!=
name|d
condition|)
block|{
name|directoryFactory
operator|.
name|doneWithDirectory
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|directoryFactory
operator|.
name|release
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|SolrIndexWriter
specifier|private
name|SolrIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|,
name|Codec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|config
operator|.
name|toIndexWriterConfig
argument_list|(
name|core
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|create
condition|?
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
else|:
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|delPolicy
argument_list|)
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Opened Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|infoStream
operator|=
name|getConfig
argument_list|()
operator|.
name|getInfoStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|numOpens
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis, commit time should be used only for debugging purposes, "
operator|+
literal|" but currently suspiciously used for replication as well"
argument_list|)
DECL|method|setCommitData
specifier|public
specifier|static
name|void
name|setCommitData
parameter_list|(
name|IndexWriter
name|iw
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Calling setCommitData with IW:"
operator|+
name|iw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|commitData
operator|.
name|put
argument_list|(
name|COMMIT_TIME_MSEC_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setLiveCommitData
argument_list|(
name|commitData
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setDirectoryFactory
specifier|private
name|void
name|setDirectoryFactory
parameter_list|(
name|DirectoryFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**    * use DocumentBuilder now...    * private final void addField(Document doc, String name, String val) {    * SchemaField ftype = schema.getField(name);    *<p/>    * // we don't check for a null val ourselves because a solr.FieldType    * // might actually want to map it to something.  If createField()    * // returns null, then we don't store the field.    *<p/>    * Field field = ftype.createField(val, boost);    * if (field != null) doc.add(field);    * }    *<p/>    *<p/>    * public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {    * Document doc = new Document();    * for (int i=0; i<fieldNames.length; i++) {    * String name = fieldNames[i];    * String val = fieldNames[i];    *<p/>    * // first null is end of list.  client can reuse arrays if they want    * // and just write a single null if there is unused space.    * if (name==null) break;    *<p/>    * addField(doc,name,val);    * }    * addDocument(doc);    * }    * ****    */
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
operator|(
name|OutOfMemoryError
operator|)
name|t
throw|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|"Error closing IndexWriter"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Rollback Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
operator|(
name|OutOfMemoryError
operator|)
name|t
throw|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|"Exception rolling back IndexWriter"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|cleanup
specifier|private
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// It's kind of an implementation detail whether
comment|// or not IndexWriter#close calls rollback, so
comment|// we assume it may or may not
name|boolean
name|doClose
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|CLOSE_LOCK
init|)
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
name|doClose
operator|=
literal|true
expr_stmt|;
name|isClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doClose
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
block|}
name|numCloses
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|directoryFactory
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"SolrIndexWriter was not closed prior to finalize()"
assert|;
name|log
operator|.
name|error
argument_list|(
literal|"SolrIndexWriter was not closed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!"
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

