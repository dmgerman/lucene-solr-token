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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FieldSelector
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
name|DefaultSimilarity
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
name|store
operator|.
name|IndexInput
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
name|IndexOutput
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
name|BitVector
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
name|*
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|SegmentReader
class|class
name|SegmentReader
extends|extends
name|IndexReader
block|{
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsReader
specifier|private
name|FieldsReader
name|fieldsReader
decl_stmt|;
DECL|field|tis
name|TermInfosReader
name|tis
decl_stmt|;
DECL|field|termVectorsReaderOrig
name|TermVectorsReader
name|termVectorsReaderOrig
init|=
literal|null
decl_stmt|;
DECL|field|termVectorsLocal
name|ThreadLocal
name|termVectorsLocal
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|deletedDocs
name|BitVector
name|deletedDocs
init|=
literal|null
decl_stmt|;
DECL|field|deletedDocsDirty
specifier|private
name|boolean
name|deletedDocsDirty
init|=
literal|false
decl_stmt|;
DECL|field|normsDirty
specifier|private
name|boolean
name|normsDirty
init|=
literal|false
decl_stmt|;
DECL|field|undeleteAll
specifier|private
name|boolean
name|undeleteAll
init|=
literal|false
decl_stmt|;
DECL|field|freqStream
name|IndexInput
name|freqStream
decl_stmt|;
DECL|field|proxStream
name|IndexInput
name|proxStream
decl_stmt|;
comment|// Compound File Reader when based on a compound file segment
DECL|field|cfsReader
name|CompoundFileReader
name|cfsReader
init|=
literal|null
decl_stmt|;
DECL|class|Norm
specifier|private
class|class
name|Norm
block|{
DECL|method|Norm
specifier|public
name|Norm
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|number
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
block|}
DECL|field|in
specifier|private
name|IndexInput
name|in
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|dirty
specifier|private
name|boolean
name|dirty
decl_stmt|;
DECL|field|number
specifier|private
name|int
name|number
decl_stmt|;
DECL|method|reWrite
specifier|private
name|void
name|reWrite
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: norms are re-written in regular directory, not cfs
name|IndexOutput
name|out
init|=
name|directory
argument_list|()
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|".tmp"
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
name|fileName
decl_stmt|;
if|if
condition|(
name|cfsReader
operator|==
literal|null
condition|)
name|fileName
operator|=
name|segment
operator|+
literal|".f"
operator|+
name|number
expr_stmt|;
else|else
block|{
comment|// use a different file name if we have compound format
name|fileName
operator|=
name|segment
operator|+
literal|".s"
operator|+
name|number
expr_stmt|;
block|}
name|directory
argument_list|()
operator|.
name|renameFile
argument_list|(
name|segment
operator|+
literal|".tmp"
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|this
operator|.
name|dirty
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|field|norms
specifier|private
name|Hashtable
name|norms
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** The class which implements SegmentReader. */
DECL|field|IMPL
specifier|private
specifier|static
name|Class
name|IMPL
decl_stmt|;
static|static
block|{
try|try
block|{
name|String
name|name
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.SegmentReader.class"
argument_list|,
name|SegmentReader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|IMPL
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot load SegmentReader class: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
try|try
block|{
name|IMPL
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|SegmentReader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot load default SegmentReader class: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|SegmentReader
specifier|protected
name|SegmentReader
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
specifier|static
name|SegmentReader
name|get
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|si
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
specifier|static
name|SegmentReader
name|get
parameter_list|(
name|SegmentInfos
name|sis
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|boolean
name|closeDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|si
argument_list|,
name|sis
argument_list|,
name|closeDir
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
specifier|static
name|SegmentReader
name|get
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|SegmentInfos
name|sis
parameter_list|,
name|boolean
name|closeDir
parameter_list|,
name|boolean
name|ownDir
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentReader
name|instance
decl_stmt|;
try|try
block|{
name|instance
operator|=
operator|(
name|SegmentReader
operator|)
name|IMPL
operator|.
name|newInstance
argument_list|()
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
name|RuntimeException
argument_list|(
literal|"cannot load SegmentReader class: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|instance
operator|.
name|init
argument_list|(
name|dir
argument_list|,
name|sis
argument_list|,
name|closeDir
argument_list|,
name|ownDir
argument_list|)
expr_stmt|;
name|instance
operator|.
name|initialize
argument_list|(
name|si
argument_list|)
expr_stmt|;
return|return
name|instance
return|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
name|segment
operator|=
name|si
operator|.
name|name
expr_stmt|;
comment|// Use compound file directory for some files, if it exists
name|Directory
name|cfsDir
init|=
name|directory
argument_list|()
decl_stmt|;
if|if
condition|(
name|directory
argument_list|()
operator|.
name|fileExists
argument_list|(
name|segment
operator|+
literal|".cfs"
argument_list|)
condition|)
block|{
name|cfsReader
operator|=
operator|new
name|CompoundFileReader
argument_list|(
name|directory
argument_list|()
argument_list|,
name|segment
operator|+
literal|".cfs"
argument_list|)
expr_stmt|;
name|cfsDir
operator|=
name|cfsReader
expr_stmt|;
block|}
comment|// No compound file exists - use the multi-file format
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|cfsDir
argument_list|,
name|segment
operator|+
literal|".fnm"
argument_list|)
expr_stmt|;
name|fieldsReader
operator|=
operator|new
name|FieldsReader
argument_list|(
name|cfsDir
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|tis
operator|=
operator|new
name|TermInfosReader
argument_list|(
name|cfsDir
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
comment|// NOTE: the bitvector is stored using the regular directory, not cfs
if|if
condition|(
name|hasDeletions
argument_list|(
name|si
argument_list|)
condition|)
name|deletedDocs
operator|=
operator|new
name|BitVector
argument_list|(
name|directory
argument_list|()
argument_list|,
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
comment|// make sure that all index files have been read or are kept open
comment|// so that if an index update removes them we'll still have them
name|freqStream
operator|=
name|cfsDir
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".frq"
argument_list|)
expr_stmt|;
name|proxStream
operator|=
name|cfsDir
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".prx"
argument_list|)
expr_stmt|;
name|openNorms
argument_list|(
name|cfsDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
comment|// open term vector files only as needed
name|termVectorsReaderOrig
operator|=
operator|new
name|TermVectorsReader
argument_list|(
name|cfsDir
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
comment|// patch for pre-1.4.2 JVMs, whose ThreadLocals leak
name|termVectorsLocal
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletedDocsDirty
condition|)
block|{
comment|// re-write deleted
name|deletedDocs
operator|.
name|write
argument_list|(
name|directory
argument_list|()
argument_list|,
name|segment
operator|+
literal|".tmp"
argument_list|)
expr_stmt|;
name|directory
argument_list|()
operator|.
name|renameFile
argument_list|(
name|segment
operator|+
literal|".tmp"
argument_list|,
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|undeleteAll
operator|&&
name|directory
argument_list|()
operator|.
name|fileExists
argument_list|(
name|segment
operator|+
literal|".del"
argument_list|)
condition|)
block|{
name|directory
argument_list|()
operator|.
name|deleteFile
argument_list|(
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normsDirty
condition|)
block|{
comment|// re-write norms
name|Enumeration
name|values
init|=
name|norms
operator|.
name|elements
argument_list|()
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|values
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|norm
operator|.
name|dirty
condition|)
block|{
name|norm
operator|.
name|reWrite
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|deletedDocsDirty
operator|=
literal|false
expr_stmt|;
name|normsDirty
operator|=
literal|false
expr_stmt|;
name|undeleteAll
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldsReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|freqStream
operator|!=
literal|null
condition|)
name|freqStream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxStream
operator|!=
literal|null
condition|)
name|proxStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeNorms
argument_list|()
expr_stmt|;
if|if
condition|(
name|termVectorsReaderOrig
operator|!=
literal|null
condition|)
name|termVectorsReaderOrig
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cfsReader
operator|!=
literal|null
condition|)
name|cfsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|hasDeletions
specifier|static
name|boolean
name|hasDeletions
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|si
operator|.
name|dir
operator|.
name|fileExists
argument_list|(
name|si
operator|.
name|name
operator|+
literal|".del"
argument_list|)
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|deletedDocs
operator|!=
literal|null
return|;
block|}
DECL|method|usesCompoundFile
specifier|static
name|boolean
name|usesCompoundFile
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|si
operator|.
name|dir
operator|.
name|fileExists
argument_list|(
name|si
operator|.
name|name
operator|+
literal|".cfs"
argument_list|)
return|;
block|}
DECL|method|hasSeparateNorms
specifier|static
name|boolean
name|hasSeparateNorms
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|result
init|=
name|si
operator|.
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
name|si
operator|.
name|name
operator|+
literal|".s"
decl_stmt|;
name|int
name|patternLength
init|=
name|pattern
operator|.
name|length
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
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|result
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
name|pattern
argument_list|)
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|result
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
name|patternLength
argument_list|)
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
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
if|if
condition|(
name|deletedDocs
operator|==
literal|null
condition|)
name|deletedDocs
operator|=
operator|new
name|BitVector
argument_list|(
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|deletedDocsDirty
operator|=
literal|true
expr_stmt|;
name|undeleteAll
operator|=
literal|false
expr_stmt|;
name|deletedDocs
operator|.
name|set
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
block|}
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
block|{
name|deletedDocs
operator|=
literal|null
expr_stmt|;
name|deletedDocsDirty
operator|=
literal|false
expr_stmt|;
name|undeleteAll
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|files
name|Vector
name|files
parameter_list|()
throws|throws
name|IOException
block|{
name|Vector
name|files
init|=
operator|new
name|Vector
argument_list|(
literal|16
argument_list|)
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
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|segment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|directory
argument_list|()
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
name|files
operator|.
name|addElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
name|String
name|name
decl_stmt|;
if|if
condition|(
name|cfsReader
operator|==
literal|null
condition|)
name|name
operator|=
name|segment
operator|+
literal|".f"
operator|+
name|i
expr_stmt|;
else|else
name|name
operator|=
name|segment
operator|+
literal|".s"
operator|+
name|i
expr_stmt|;
if|if
condition|(
name|directory
argument_list|()
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
name|files
operator|.
name|addElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|files
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
block|{
return|return
name|tis
operator|.
name|terms
argument_list|()
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tis
operator|.
name|terms
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|document
specifier|public
specifier|synchronized
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isDeleted
argument_list|(
name|n
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"attempt to access a deleted document"
argument_list|)
throw|;
return|return
name|fieldsReader
operator|.
name|doc
argument_list|(
name|n
argument_list|,
name|fieldSelector
argument_list|)
return|;
block|}
DECL|method|isDeleted
specifier|public
specifier|synchronized
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
operator|(
name|deletedDocs
operator|!=
literal|null
operator|&&
name|deletedDocs
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|)
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermDocs
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermPositions
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
init|=
name|tis
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
return|return
name|ti
operator|.
name|docFreq
return|;
else|else
return|return
literal|0
return|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
name|int
name|n
init|=
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|deletedDocs
operator|!=
literal|null
condition|)
name|n
operator|-=
name|deletedDocs
operator|.
name|count
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|fieldsReader
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames(IndexReader.FieldOption fldOption)    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldOption
parameter_list|)
block|{
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fi
operator|.
name|isIndexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_NO_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeTermVector
operator|==
literal|true
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|)
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldSet
return|;
block|}
DECL|method|hasNorms
specifier|public
specifier|synchronized
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|norms
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|createFakeNorms
specifier|static
name|byte
index|[]
name|createFakeNorms
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|ones
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ones
argument_list|,
name|DefaultSimilarity
operator|.
name|encodeNorm
argument_list|(
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ones
return|;
block|}
DECL|field|ones
specifier|private
name|byte
index|[]
name|ones
decl_stmt|;
DECL|method|fakeNorms
specifier|private
name|byte
index|[]
name|fakeNorms
parameter_list|()
block|{
if|if
condition|(
name|ones
operator|==
literal|null
condition|)
name|ones
operator|=
name|createFakeNorms
argument_list|(
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ones
return|;
block|}
comment|// can return null if norms aren't stored
DECL|method|getNorms
specifier|protected
specifier|synchronized
name|byte
index|[]
name|getNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// not indexed, or norms not stored
if|if
condition|(
name|norm
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
comment|// value not yet read
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|norms
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|norm
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
comment|// cache it
block|}
return|return
name|norm
operator|.
name|bytes
return|;
block|}
comment|// returns fake norms if norms aren't available
DECL|method|norms
specifier|public
specifier|synchronized
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
name|getNorms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
name|bytes
operator|=
name|fakeNorms
argument_list|()
expr_stmt|;
return|return
name|bytes
return|;
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm
operator|==
literal|null
condition|)
comment|// not an indexed field
return|return;
name|norm
operator|.
name|dirty
operator|=
literal|true
expr_stmt|;
comment|// mark it dirty
name|normsDirty
operator|=
literal|true
expr_stmt|;
name|norms
argument_list|(
name|field
argument_list|)
index|[
name|doc
index|]
operator|=
name|value
expr_stmt|;
comment|// set the value
block|}
comment|/** Read norms into a pre-allocated array. */
DECL|method|norms
specifier|public
specifier|synchronized
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|fakeNorms
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|norm
operator|.
name|bytes
operator|!=
literal|null
condition|)
block|{
comment|// can copy from cache
name|System
operator|.
name|arraycopy
argument_list|(
name|norm
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|IndexInput
name|normStream
init|=
operator|(
name|IndexInput
operator|)
name|norm
operator|.
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
try|try
block|{
comment|// read from disk
name|normStream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|normStream
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|normStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|openNorms
specifier|private
name|void
name|openNorms
parameter_list|(
name|Directory
name|cfsDir
parameter_list|)
throws|throws
name|IOException
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
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
comment|// look first if there are separate norms in compound format
name|String
name|fileName
init|=
name|segment
operator|+
literal|".s"
operator|+
name|fi
operator|.
name|number
decl_stmt|;
name|Directory
name|d
init|=
name|directory
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|d
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|fileName
operator|=
name|segment
operator|+
literal|".f"
operator|+
name|fi
operator|.
name|number
expr_stmt|;
name|d
operator|=
name|cfsDir
expr_stmt|;
block|}
name|norms
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
operator|new
name|Norm
argument_list|(
name|d
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|fi
operator|.
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|closeNorms
specifier|private
name|void
name|closeNorms
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|norms
init|)
block|{
name|Enumeration
name|enumerator
init|=
name|norms
operator|.
name|elements
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumerator
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|enumerator
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|norm
operator|.
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a clone from the initial TermVectorsReader and store it in the ThreadLocal.    * @return TermVectorsReader    */
DECL|method|getTermVectorsReader
specifier|private
name|TermVectorsReader
name|getTermVectorsReader
parameter_list|()
block|{
name|TermVectorsReader
name|tvReader
init|=
operator|(
name|TermVectorsReader
operator|)
name|termVectorsLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|tvReader
operator|==
literal|null
condition|)
block|{
name|tvReader
operator|=
operator|(
name|TermVectorsReader
operator|)
name|termVectorsReaderOrig
operator|.
name|clone
argument_list|()
expr_stmt|;
name|termVectorsLocal
operator|.
name|set
argument_list|(
name|tvReader
argument_list|)
expr_stmt|;
block|}
return|return
name|tvReader
return|;
block|}
comment|/** Return a term frequency vector for the specified document and field. The    *  vector returned contains term numbers and frequencies for all terms in    *  the specified field of this document, if the field had storeTermVector    *  flag set.  If the flag was not set, the method returns null.    * @throws IOException    */
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if this field is invalid or has no stored term vector
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
operator|||
operator|!
name|fi
operator|.
name|storeTermVector
operator|||
name|termVectorsReaderOrig
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermVectorsReader
name|termVectorsReader
init|=
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|termVectorsReader
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|termVectorsReader
operator|.
name|get
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|)
return|;
block|}
comment|/** Return an array of term frequency vectors for the specified document.    *  The array contains a vector for each vectorized field in the document.    *  Each vector vector contains term numbers and frequencies for all terms    *  in a given vectorized field.    *  If no such fields existed, the method returns null.    * @throws IOException    */
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|docNumber
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termVectorsReaderOrig
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermVectorsReader
name|termVectorsReader
init|=
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|termVectorsReader
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|termVectorsReader
operator|.
name|get
argument_list|(
name|docNumber
argument_list|)
return|;
block|}
block|}
end_class

end_unit

