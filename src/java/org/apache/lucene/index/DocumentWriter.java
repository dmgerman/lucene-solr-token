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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|TokenStream
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
name|Token
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
name|search
operator|.
name|Similarity
import|;
end_import

begin_class
DECL|class|DocumentWriter
specifier|final
class|class
name|DocumentWriter
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|maxFieldLength
specifier|private
name|int
name|maxFieldLength
decl_stmt|;
comment|/**    *     * @param directory The directory to write the document information to    * @param analyzer The analyzer to use for the document    * @param similarity The Similarity function    * @param maxFieldLength The maximum number of tokens a field may have    */
DECL|method|DocumentWriter
name|DocumentWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|int
name|maxFieldLength
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
name|this
operator|.
name|maxFieldLength
operator|=
name|maxFieldLength
expr_stmt|;
block|}
DECL|method|addDocument
specifier|final
name|void
name|addDocument
parameter_list|(
name|String
name|segment
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write field names
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|()
expr_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
comment|// write field values
name|FieldsWriter
name|fieldsWriter
init|=
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
name|fieldsWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// invert doc into postingTable
name|postingTable
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// clear postingTable
name|fieldLengths
operator|=
operator|new
name|int
index|[
name|fieldInfos
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
comment|// init fieldLengths
name|fieldPositions
operator|=
operator|new
name|int
index|[
name|fieldInfos
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
comment|// init fieldPositions
name|fieldBoosts
operator|=
operator|new
name|float
index|[
name|fieldInfos
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
comment|// init fieldBoosts
name|Arrays
operator|.
name|fill
argument_list|(
name|fieldBoosts
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|invertDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// sort postingTable into an array
name|Posting
index|[]
name|postings
init|=
name|sortPostingTable
argument_list|()
decl_stmt|;
comment|/*     for (int i = 0; i< postings.length; i++) {       Posting posting = postings[i];       System.out.print(posting.term);       System.out.print(" freq=" + posting.freq);       System.out.print(" pos=");       System.out.print(posting.positions[0]);       for (int j = 1; j< posting.freq; j++) 	System.out.print("," + posting.positions[j]);       System.out.println("");     }     */
comment|// write postings
name|writePostings
argument_list|(
name|postings
argument_list|,
name|segment
argument_list|)
expr_stmt|;
comment|// write norms of indexed fields
name|writeNorms
argument_list|(
name|doc
argument_list|,
name|segment
argument_list|)
expr_stmt|;
block|}
comment|// Keys are Terms, values are Postings.
comment|// Used to buffer a document before it is written to the index.
DECL|field|postingTable
specifier|private
specifier|final
name|Hashtable
name|postingTable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|fieldLengths
specifier|private
name|int
index|[]
name|fieldLengths
decl_stmt|;
DECL|field|fieldPositions
specifier|private
name|int
index|[]
name|fieldPositions
decl_stmt|;
DECL|field|fieldBoosts
specifier|private
name|float
index|[]
name|fieldBoosts
decl_stmt|;
comment|// Tokenizes the fields of a document into Postings.
DECL|method|invertDocument
specifier|private
specifier|final
name|void
name|invertDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
name|int
name|fieldNumber
init|=
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|fieldLengths
index|[
name|fieldNumber
index|]
decl_stmt|;
comment|// length of field
name|int
name|position
init|=
name|fieldPositions
index|[
name|fieldNumber
index|]
decl_stmt|;
comment|// position in field
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
block|{
comment|// un-tokenized field
name|addPosition
argument_list|(
name|fieldName
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|,
name|position
operator|++
argument_list|)
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
name|Reader
name|reader
decl_stmt|;
comment|// find or make Reader
if|if
condition|(
name|field
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
name|field
operator|.
name|readerValue
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must have either String or Reader value"
argument_list|)
throw|;
comment|// Tokenize field and add to postingTable
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|Token
name|t
init|=
name|stream
operator|.
name|next
argument_list|()
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|stream
operator|.
name|next
argument_list|()
control|)
block|{
name|position
operator|+=
operator|(
name|t
operator|.
name|getPositionIncrement
argument_list|()
operator|-
literal|1
operator|)
expr_stmt|;
name|addPosition
argument_list|(
name|fieldName
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|,
name|position
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|length
operator|>
name|maxFieldLength
condition|)
break|break;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|fieldLengths
index|[
name|fieldNumber
index|]
operator|=
name|length
expr_stmt|;
comment|// save field length
name|fieldPositions
index|[
name|fieldNumber
index|]
operator|=
name|position
expr_stmt|;
comment|// save field position
name|fieldBoosts
index|[
name|fieldNumber
index|]
operator|*=
name|field
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|termBuffer
specifier|private
specifier|final
name|Term
name|termBuffer
init|=
operator|new
name|Term
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// avoid consing
DECL|method|addPosition
specifier|private
specifier|final
name|void
name|addPosition
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|termBuffer
operator|.
name|set
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|Posting
name|ti
init|=
operator|(
name|Posting
operator|)
name|postingTable
operator|.
name|get
argument_list|(
name|termBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
block|{
comment|// word seen before
name|int
name|freq
init|=
name|ti
operator|.
name|freq
decl_stmt|;
if|if
condition|(
name|ti
operator|.
name|positions
operator|.
name|length
operator|==
name|freq
condition|)
block|{
comment|// positions array is full
name|int
index|[]
name|newPositions
init|=
operator|new
name|int
index|[
name|freq
operator|*
literal|2
index|]
decl_stmt|;
comment|// double size
name|int
index|[]
name|positions
init|=
name|ti
operator|.
name|positions
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
name|freq
condition|;
name|i
operator|++
control|)
comment|// copy old positions to new
name|newPositions
index|[
name|i
index|]
operator|=
name|positions
index|[
name|i
index|]
expr_stmt|;
name|ti
operator|.
name|positions
operator|=
name|newPositions
expr_stmt|;
block|}
name|ti
operator|.
name|positions
index|[
name|freq
index|]
operator|=
name|position
expr_stmt|;
comment|// add new position
name|ti
operator|.
name|freq
operator|=
name|freq
operator|+
literal|1
expr_stmt|;
comment|// update frequency
block|}
else|else
block|{
comment|// word not seen before
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|postingTable
operator|.
name|put
argument_list|(
name|term
argument_list|,
operator|new
name|Posting
argument_list|(
name|term
argument_list|,
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sortPostingTable
specifier|private
specifier|final
name|Posting
index|[]
name|sortPostingTable
parameter_list|()
block|{
comment|// copy postingTable into an array
name|Posting
index|[]
name|array
init|=
operator|new
name|Posting
index|[
name|postingTable
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Enumeration
name|postings
init|=
name|postingTable
operator|.
name|elements
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|postings
operator|.
name|hasMoreElements
argument_list|()
condition|;
name|i
operator|++
control|)
name|array
index|[
name|i
index|]
operator|=
operator|(
name|Posting
operator|)
name|postings
operator|.
name|nextElement
argument_list|()
expr_stmt|;
comment|// sort the array
name|quickSort
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
DECL|method|quickSort
specifier|private
specifier|static
specifier|final
name|void
name|quickSort
parameter_list|(
name|Posting
index|[]
name|postings
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|lo
operator|>=
name|hi
condition|)
return|return;
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|postings
index|[
name|lo
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|postings
index|[
name|mid
index|]
operator|.
name|term
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Posting
name|tmp
init|=
name|postings
index|[
name|lo
index|]
decl_stmt|;
name|postings
index|[
name|lo
index|]
operator|=
name|postings
index|[
name|mid
index|]
expr_stmt|;
name|postings
index|[
name|mid
index|]
operator|=
name|tmp
expr_stmt|;
block|}
if|if
condition|(
name|postings
index|[
name|mid
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|postings
index|[
name|hi
index|]
operator|.
name|term
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Posting
name|tmp
init|=
name|postings
index|[
name|mid
index|]
decl_stmt|;
name|postings
index|[
name|mid
index|]
operator|=
name|postings
index|[
name|hi
index|]
expr_stmt|;
name|postings
index|[
name|hi
index|]
operator|=
name|tmp
expr_stmt|;
if|if
condition|(
name|postings
index|[
name|lo
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|postings
index|[
name|mid
index|]
operator|.
name|term
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Posting
name|tmp2
init|=
name|postings
index|[
name|lo
index|]
decl_stmt|;
name|postings
index|[
name|lo
index|]
operator|=
name|postings
index|[
name|mid
index|]
expr_stmt|;
name|postings
index|[
name|mid
index|]
operator|=
name|tmp2
expr_stmt|;
block|}
block|}
name|int
name|left
init|=
name|lo
operator|+
literal|1
decl_stmt|;
name|int
name|right
init|=
name|hi
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|left
operator|>=
name|right
condition|)
return|return;
name|Term
name|partition
init|=
name|postings
index|[
name|mid
index|]
operator|.
name|term
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
while|while
condition|(
name|postings
index|[
name|right
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|partition
argument_list|)
operator|>
literal|0
condition|)
operator|--
name|right
expr_stmt|;
while|while
condition|(
name|left
operator|<
name|right
operator|&&
name|postings
index|[
name|left
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|partition
argument_list|)
operator|<=
literal|0
condition|)
operator|++
name|left
expr_stmt|;
if|if
condition|(
name|left
operator|<
name|right
condition|)
block|{
name|Posting
name|tmp
init|=
name|postings
index|[
name|left
index|]
decl_stmt|;
name|postings
index|[
name|left
index|]
operator|=
name|postings
index|[
name|right
index|]
expr_stmt|;
name|postings
index|[
name|right
index|]
operator|=
name|tmp
expr_stmt|;
operator|--
name|right
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|quickSort
argument_list|(
name|postings
argument_list|,
name|lo
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|quickSort
argument_list|(
name|postings
argument_list|,
name|left
operator|+
literal|1
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
DECL|method|writePostings
specifier|private
specifier|final
name|void
name|writePostings
parameter_list|(
name|Posting
index|[]
name|postings
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|freq
init|=
literal|null
decl_stmt|,
name|prox
init|=
literal|null
decl_stmt|;
name|TermInfosWriter
name|tis
init|=
literal|null
decl_stmt|;
name|TermVectorsWriter
name|termVectorWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//open files for inverse index storage
name|freq
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
name|prox
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
name|tis
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
name|TermInfo
name|ti
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
name|String
name|currentField
init|=
literal|null
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
name|postings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Posting
name|posting
init|=
name|postings
index|[
name|i
index|]
decl_stmt|;
comment|// add an entry to the dictionary with pointers to prox and freq files
name|ti
operator|.
name|set
argument_list|(
literal|1
argument_list|,
name|freq
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|prox
operator|.
name|getFilePointer
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|tis
operator|.
name|add
argument_list|(
name|posting
operator|.
name|term
argument_list|,
name|ti
argument_list|)
expr_stmt|;
comment|// add an entry to the freq file
name|int
name|postingFreq
init|=
name|posting
operator|.
name|freq
decl_stmt|;
if|if
condition|(
name|postingFreq
operator|==
literal|1
condition|)
comment|// optimize freq=1
name|freq
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// set low bit of doc num.
else|else
block|{
name|freq
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// the document number
name|freq
operator|.
name|writeVInt
argument_list|(
name|postingFreq
argument_list|)
expr_stmt|;
comment|// frequency in doc
block|}
name|int
name|lastPosition
init|=
literal|0
decl_stmt|;
comment|// write positions
name|int
index|[]
name|positions
init|=
name|posting
operator|.
name|positions
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
name|postingFreq
condition|;
name|j
operator|++
control|)
block|{
comment|// use delta-encoding
name|int
name|position
init|=
name|positions
index|[
name|j
index|]
decl_stmt|;
name|prox
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
comment|// check to see if we switched to a new field
name|String
name|termField
init|=
name|posting
operator|.
name|term
operator|.
name|field
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentField
operator|!=
name|termField
condition|)
block|{
comment|// changing field - see if there is something to save
name|currentField
operator|=
name|termField
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|currentField
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|storeTermVector
condition|)
block|{
if|if
condition|(
name|termVectorWriter
operator|==
literal|null
condition|)
block|{
name|termVectorWriter
operator|=
operator|new
name|TermVectorsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|termVectorWriter
operator|.
name|openDocument
argument_list|()
expr_stmt|;
block|}
name|termVectorWriter
operator|.
name|openField
argument_list|(
name|currentField
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termVectorWriter
operator|!=
literal|null
condition|)
block|{
name|termVectorWriter
operator|.
name|closeField
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termVectorWriter
operator|!=
literal|null
operator|&&
name|termVectorWriter
operator|.
name|isFieldOpen
argument_list|()
condition|)
block|{
name|termVectorWriter
operator|.
name|addTerm
argument_list|(
name|posting
operator|.
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|postingFreq
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termVectorWriter
operator|!=
literal|null
condition|)
name|termVectorWriter
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// make an effort to close all streams we can but remember and re-throw
comment|// the first exception encountered in this process
name|IOException
name|keep
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|freq
operator|!=
literal|null
condition|)
try|try
block|{
name|freq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|prox
operator|!=
literal|null
condition|)
try|try
block|{
name|prox
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
try|try
block|{
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|termVectorWriter
operator|!=
literal|null
condition|)
try|try
block|{
name|termVectorWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|keep
operator|.
name|fillInStackTrace
argument_list|()
throw|;
block|}
block|}
DECL|method|writeNorms
specifier|private
specifier|final
name|void
name|writeNorms
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|IOException
block|{
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
name|int
name|n
init|=
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|float
name|norm
init|=
name|fieldBoosts
index|[
name|n
index|]
operator|*
name|similarity
operator|.
name|lengthNorm
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|fieldLengths
index|[
name|n
index|]
argument_list|)
decl_stmt|;
name|OutputStream
name|norms
init|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
literal|".f"
operator|+
name|n
argument_list|)
decl_stmt|;
try|try
block|{
name|norms
operator|.
name|writeByte
argument_list|(
name|similarity
operator|.
name|encodeNorm
argument_list|(
name|norm
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|norms
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

begin_class
DECL|class|Posting
specifier|final
class|class
name|Posting
block|{
comment|// info about a Term in a doc
DECL|field|term
name|Term
name|term
decl_stmt|;
comment|// the Term
DECL|field|freq
name|int
name|freq
decl_stmt|;
comment|// its frequency in doc
DECL|field|positions
name|int
index|[]
name|positions
decl_stmt|;
comment|// positions it occurs at
DECL|method|Posting
name|Posting
parameter_list|(
name|Term
name|t
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|term
operator|=
name|t
expr_stmt|;
name|freq
operator|=
literal|1
expr_stmt|;
name|positions
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|positions
index|[
literal|0
index|]
operator|=
name|position
expr_stmt|;
block|}
block|}
end_class

end_unit

