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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|OutputStream
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
name|InputStream
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
name|util
operator|.
name|PriorityQueue
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

begin_class
DECL|class|SegmentMerger
specifier|final
class|class
name|SegmentMerger
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|readers
specifier|private
name|Vector
name|readers
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|method|SegmentMerger
name|SegmentMerger
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|name
expr_stmt|;
block|}
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|SegmentReader
name|reader
parameter_list|)
block|{
name|readers
operator|.
name|addElement
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|segmentReader
specifier|final
name|SegmentReader
name|segmentReader
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|merge
specifier|final
name|void
name|merge
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|mergeFields
argument_list|()
expr_stmt|;
name|mergeTerms
argument_list|()
expr_stmt|;
name|mergeNorms
argument_list|()
expr_stmt|;
block|}
finally|finally
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// close readers
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeFields
specifier|private
specifier|final
name|void
name|mergeFields
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|()
expr_stmt|;
comment|// merge field names
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|reader
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
name|fieldInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|segment
operator|+
literal|".fnm"
argument_list|)
expr_stmt|;
name|FieldsWriter
name|fieldsWriter
init|=
comment|// merge field values
operator|new
name|FieldsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
try|try
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|BitVector
name|deletedDocs
init|=
name|reader
operator|.
name|deletedDocs
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
if|if
condition|(
name|deletedDocs
operator|==
literal|null
operator|||
operator|!
name|deletedDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
comment|// skip deleted docs
name|fieldsWriter
operator|.
name|addDocument
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|freqOutput
specifier|private
name|OutputStream
name|freqOutput
init|=
literal|null
decl_stmt|;
DECL|field|proxOutput
specifier|private
name|OutputStream
name|proxOutput
init|=
literal|null
decl_stmt|;
DECL|field|termInfosWriter
specifier|private
name|TermInfosWriter
name|termInfosWriter
init|=
literal|null
decl_stmt|;
DECL|field|queue
specifier|private
name|SegmentMergeQueue
name|queue
init|=
literal|null
decl_stmt|;
DECL|method|mergeTerms
specifier|private
specifier|final
name|void
name|mergeTerms
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|freqOutput
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".frq"
argument_list|)
expr_stmt|;
name|proxOutput
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".prx"
argument_list|)
expr_stmt|;
name|termInfosWriter
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|mergeTermInfos
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|freqOutput
operator|!=
literal|null
condition|)
name|freqOutput
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxOutput
operator|!=
literal|null
condition|)
name|proxOutput
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|termInfosWriter
operator|!=
literal|null
condition|)
name|termInfosWriter
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeTermInfos
specifier|private
specifier|final
name|void
name|mergeTermInfos
parameter_list|()
throws|throws
name|IOException
block|{
name|queue
operator|=
operator|new
name|SegmentMergeQueue
argument_list|(
name|readers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|base
init|=
literal|0
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SegmentTermEnum
name|termEnum
init|=
operator|(
name|SegmentTermEnum
operator|)
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|SegmentMergeInfo
name|smi
init|=
operator|new
name|SegmentMergeInfo
argument_list|(
name|base
argument_list|,
name|termEnum
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|base
operator|+=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
if|if
condition|(
name|smi
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// initialize queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|SegmentMergeInfo
index|[]
name|match
init|=
operator|new
name|SegmentMergeInfo
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|matchSize
init|=
literal|0
decl_stmt|;
comment|// pop matching terms
name|match
index|[
name|matchSize
operator|++
index|]
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|Term
name|term
init|=
name|match
index|[
literal|0
index|]
operator|.
name|term
decl_stmt|;
name|SegmentMergeInfo
name|top
init|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
while|while
condition|(
name|top
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|top
operator|.
name|term
argument_list|)
operator|==
literal|0
condition|)
block|{
name|match
index|[
name|matchSize
operator|++
index|]
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|top
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
name|mergeTermInfo
argument_list|(
name|match
argument_list|,
name|matchSize
argument_list|)
expr_stmt|;
comment|// add new TermInfo
while|while
condition|(
name|matchSize
operator|>
literal|0
condition|)
block|{
name|SegmentMergeInfo
name|smi
init|=
name|match
index|[
operator|--
name|matchSize
index|]
decl_stmt|;
if|if
condition|(
name|smi
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// restore queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// done with a segment
block|}
block|}
block|}
DECL|field|termInfo
specifier|private
specifier|final
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
comment|// minimize consing
DECL|method|mergeTermInfo
specifier|private
specifier|final
name|void
name|mergeTermInfo
parameter_list|(
name|SegmentMergeInfo
index|[]
name|smis
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|freqPointer
init|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|proxPointer
init|=
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|int
name|df
init|=
name|appendPostings
argument_list|(
name|smis
argument_list|,
name|n
argument_list|)
decl_stmt|;
comment|// append posting data
if|if
condition|(
name|df
operator|>
literal|0
condition|)
block|{
comment|// add an entry to the dictionary with pointers to prox and freq files
name|termInfo
operator|.
name|set
argument_list|(
name|df
argument_list|,
name|freqPointer
argument_list|,
name|proxPointer
argument_list|)
expr_stmt|;
name|termInfosWriter
operator|.
name|add
argument_list|(
name|smis
index|[
literal|0
index|]
operator|.
name|term
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendPostings
specifier|private
specifier|final
name|int
name|appendPostings
parameter_list|(
name|SegmentMergeInfo
index|[]
name|smis
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
name|int
name|df
init|=
literal|0
decl_stmt|;
comment|// number of docs w/ term
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|SegmentMergeInfo
name|smi
init|=
name|smis
index|[
name|i
index|]
decl_stmt|;
name|SegmentTermPositions
name|postings
init|=
name|smi
operator|.
name|postings
decl_stmt|;
name|int
name|base
init|=
name|smi
operator|.
name|base
decl_stmt|;
name|int
index|[]
name|docMap
init|=
name|smi
operator|.
name|docMap
decl_stmt|;
name|smi
operator|.
name|termEnum
operator|.
name|termInfo
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
name|postings
operator|.
name|seek
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
while|while
condition|(
name|postings
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|doc
decl_stmt|;
if|if
condition|(
name|docMap
operator|==
literal|null
condition|)
name|doc
operator|=
name|base
operator|+
name|postings
operator|.
name|doc
expr_stmt|;
comment|// no deletions
else|else
name|doc
operator|=
name|base
operator|+
name|docMap
index|[
name|postings
operator|.
name|doc
index|]
expr_stmt|;
comment|// re-map around deletions
if|if
condition|(
name|doc
operator|<
name|lastDoc
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"docs out of order"
argument_list|)
throw|;
name|int
name|docCode
init|=
operator|(
name|doc
operator|-
name|lastDoc
operator|)
operator|<<
literal|1
decl_stmt|;
comment|// use low bit to flag freq=1
name|lastDoc
operator|=
name|doc
expr_stmt|;
name|int
name|freq
init|=
name|postings
operator|.
name|freq
decl_stmt|;
if|if
condition|(
name|freq
operator|==
literal|1
condition|)
block|{
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|docCode
operator||
literal|1
argument_list|)
expr_stmt|;
comment|// write doc& freq=1
block|}
else|else
block|{
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|docCode
argument_list|)
expr_stmt|;
comment|// write doc
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
comment|// write frequency in doc
block|}
name|int
name|lastPosition
init|=
literal|0
decl_stmt|;
comment|// write position deltas
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|position
init|=
name|postings
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|proxOutput
operator|.
name|writeVInt
argument_list|(
name|position
operator|-
name|lastPosition
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
block|}
name|df
operator|++
expr_stmt|;
block|}
block|}
return|return
name|df
return|;
block|}
DECL|method|mergeNorms
specifier|private
specifier|final
name|void
name|mergeNorms
parameter_list|()
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
condition|)
block|{
name|OutputStream
name|output
init|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".f"
operator|+
name|i
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|BitVector
name|deletedDocs
init|=
name|reader
operator|.
name|deletedDocs
decl_stmt|;
name|InputStream
name|input
init|=
name|reader
operator|.
name|normStream
argument_list|(
name|fi
operator|.
name|name
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|maxDoc
condition|;
name|k
operator|++
control|)
block|{
name|byte
name|norm
init|=
name|input
operator|!=
literal|null
condition|?
name|input
operator|.
name|readByte
argument_list|()
else|:
operator|(
name|byte
operator|)
literal|0
decl_stmt|;
if|if
condition|(
name|deletedDocs
operator|==
literal|null
operator|||
operator|!
name|deletedDocs
operator|.
name|get
argument_list|(
name|k
argument_list|)
condition|)
name|output
operator|.
name|writeByte
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

