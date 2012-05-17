begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|SegmentInfosWriter
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
name|SegmentInfo
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
name|SegmentInfos
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
name|ChecksumIndexOutput
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
name|FlushInfo
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
name|IOContext
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
name|BytesRef
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * writes plaintext segments files  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextSegmentInfosWriter
specifier|public
class|class
name|SimpleTextSegmentInfosWriter
extends|extends
name|SegmentInfosWriter
block|{
DECL|field|VERSION
specifier|final
specifier|static
name|BytesRef
name|VERSION
init|=
operator|new
name|BytesRef
argument_list|(
literal|"version "
argument_list|)
decl_stmt|;
DECL|field|COUNTER
specifier|final
specifier|static
name|BytesRef
name|COUNTER
init|=
operator|new
name|BytesRef
argument_list|(
literal|"counter "
argument_list|)
decl_stmt|;
DECL|field|NUM_USERDATA
specifier|final
specifier|static
name|BytesRef
name|NUM_USERDATA
init|=
operator|new
name|BytesRef
argument_list|(
literal|"user data entries "
argument_list|)
decl_stmt|;
DECL|field|USERDATA_KEY
specifier|final
specifier|static
name|BytesRef
name|USERDATA_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  key "
argument_list|)
decl_stmt|;
DECL|field|USERDATA_VALUE
specifier|final
specifier|static
name|BytesRef
name|USERDATA_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  value "
argument_list|)
decl_stmt|;
DECL|field|NUM_SEGMENTS
specifier|final
specifier|static
name|BytesRef
name|NUM_SEGMENTS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"number of segments "
argument_list|)
decl_stmt|;
DECL|field|SI_NAME
specifier|final
specifier|static
name|BytesRef
name|SI_NAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  name "
argument_list|)
decl_stmt|;
DECL|field|SI_CODEC
specifier|final
specifier|static
name|BytesRef
name|SI_CODEC
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    codec "
argument_list|)
decl_stmt|;
DECL|field|SI_VERSION
specifier|final
specifier|static
name|BytesRef
name|SI_VERSION
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    version "
argument_list|)
decl_stmt|;
DECL|field|SI_DOCCOUNT
specifier|final
specifier|static
name|BytesRef
name|SI_DOCCOUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    number of documents "
argument_list|)
decl_stmt|;
DECL|field|SI_DELCOUNT
specifier|final
specifier|static
name|BytesRef
name|SI_DELCOUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    number of deletions "
argument_list|)
decl_stmt|;
DECL|field|SI_USECOMPOUND
specifier|final
specifier|static
name|BytesRef
name|SI_USECOMPOUND
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    uses compound file "
argument_list|)
decl_stmt|;
DECL|field|SI_DSOFFSET
specifier|final
specifier|static
name|BytesRef
name|SI_DSOFFSET
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    docstore offset "
argument_list|)
decl_stmt|;
DECL|field|SI_DSSEGMENT
specifier|final
specifier|static
name|BytesRef
name|SI_DSSEGMENT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    docstore segment "
argument_list|)
decl_stmt|;
DECL|field|SI_DSCOMPOUND
specifier|final
specifier|static
name|BytesRef
name|SI_DSCOMPOUND
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    docstore is compound file "
argument_list|)
decl_stmt|;
DECL|field|SI_DELGEN
specifier|final
specifier|static
name|BytesRef
name|SI_DELGEN
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    deletion generation "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_NORMGEN
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_NORMGEN
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    norms generations "
argument_list|)
decl_stmt|;
DECL|field|SI_NORMGEN_KEY
specifier|final
specifier|static
name|BytesRef
name|SI_NORMGEN_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      key "
argument_list|)
decl_stmt|;
DECL|field|SI_NORMGEN_VALUE
specifier|final
specifier|static
name|BytesRef
name|SI_NORMGEN_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      value "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_DIAG
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_DIAG
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    diagnostics "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_KEY
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      key "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_VALUE
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      value "
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|writeInfos
specifier|public
name|IndexOutput
name|writeInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentsFileName
parameter_list|,
name|String
name|codecID
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|IndexOutput
name|out
init|=
operator|new
name|ChecksumIndexOutput
argument_list|(
name|dir
operator|.
name|createOutput
argument_list|(
name|segmentsFileName
argument_list|,
operator|new
name|IOContext
argument_list|(
operator|new
name|FlushInfo
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|,
name|infos
operator|.
name|totalDocCount
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// required preamble:
name|out
operator|.
name|writeInt
argument_list|(
name|SegmentInfos
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
comment|// write FORMAT
name|out
operator|.
name|writeString
argument_list|(
name|codecID
argument_list|)
expr_stmt|;
comment|// write codecID
comment|// end preamble
comment|// version
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|infos
operator|.
name|version
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// counter
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|COUNTER
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|infos
operator|.
name|counter
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// user data
name|int
name|numUserDataEntries
init|=
name|infos
operator|.
name|getUserData
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|infos
operator|.
name|getUserData
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUM_USERDATA
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numUserDataEntries
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|numUserDataEntries
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userEntry
range|:
name|infos
operator|.
name|getUserData
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|USERDATA_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|userEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|USERDATA_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|userEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|// infos size
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUM_SEGMENTS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|SegmentInfo
name|si
range|:
name|infos
control|)
block|{
name|writeInfo
argument_list|(
name|out
argument_list|,
name|si
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|out
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeInfo
specifier|private
name|void
name|writeInfo
parameter_list|(
name|IndexOutput
name|output
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|si
operator|.
name|getDelCount
argument_list|()
operator|<=
name|si
operator|.
name|docCount
operator|:
literal|"delCount="
operator|+
name|si
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" docCount="
operator|+
name|si
operator|.
name|docCount
operator|+
literal|" segment="
operator|+
name|si
operator|.
name|name
assert|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NAME
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|name
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_CODEC
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getCodec
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_VERSION
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getVersion
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DOCCOUNT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|si
operator|.
name|docCount
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DELCOUNT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_USECOMPOUND
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DSOFFSET
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DSSEGMENT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DSCOMPOUND
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DELGEN
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDelGen
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
init|=
name|si
operator|.
name|getNormGen
argument_list|()
decl_stmt|;
name|int
name|numNormGen
init|=
name|normGen
operator|==
literal|null
condition|?
literal|0
else|:
name|normGen
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_NORMGEN
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numNormGen
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numNormGen
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|normGen
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NORMGEN_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NORMGEN_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|si
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|int
name|numDiagnostics
init|=
name|diagnostics
operator|==
literal|null
condition|?
literal|0
else|:
name|diagnostics
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_DIAG
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numDiagnostics
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDiagnostics
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagEntry
range|:
name|diagnostics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|out
operator|)
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishCommit
specifier|public
name|void
name|finishCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|out
operator|)
operator|.
name|finishCommit
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

