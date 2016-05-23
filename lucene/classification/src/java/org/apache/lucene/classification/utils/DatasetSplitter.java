begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
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
name|HashMap
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
name|TextField
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
name|index
operator|.
name|IndexableField
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
name|LeafReader
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
name|SortedDocValues
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
name|Terms
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
name|IndexSearcher
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
name|MatchAllDocsQuery
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
name|ScoreDoc
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
name|Sort
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
name|grouping
operator|.
name|GroupDocs
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
name|grouping
operator|.
name|GroupingSearch
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
name|grouping
operator|.
name|TopGroups
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

begin_comment
comment|/**  * Utility class for creating training / test / cross validation indexes from the original index.  */
end_comment

begin_class
DECL|class|DatasetSplitter
specifier|public
class|class
name|DatasetSplitter
block|{
DECL|field|crossValidationRatio
specifier|private
specifier|final
name|double
name|crossValidationRatio
decl_stmt|;
DECL|field|testRatio
specifier|private
specifier|final
name|double
name|testRatio
decl_stmt|;
comment|/**    * Create a {@link DatasetSplitter} by giving test and cross validation IDXs sizes    *    * @param testRatio            the ratio of the original index to be used for the test IDX as a<code>double</code> between 0.0 and 1.0    * @param crossValidationRatio the ratio of the original index to be used for the c.v. IDX as a<code>double</code> between 0.0 and 1.0    */
DECL|method|DatasetSplitter
specifier|public
name|DatasetSplitter
parameter_list|(
name|double
name|testRatio
parameter_list|,
name|double
name|crossValidationRatio
parameter_list|)
block|{
name|this
operator|.
name|crossValidationRatio
operator|=
name|crossValidationRatio
expr_stmt|;
name|this
operator|.
name|testRatio
operator|=
name|testRatio
expr_stmt|;
block|}
comment|/**    * Split a given index into 3 indexes for training, test and cross validation tasks respectively    *    * @param originalIndex        an {@link org.apache.lucene.index.LeafReader} on the source index    * @param trainingIndex        a {@link Directory} used to write the training index    * @param testIndex            a {@link Directory} used to write the test index    * @param crossValidationIndex a {@link Directory} used to write the cross validation index    * @param analyzer             {@link Analyzer} used to create the new docs    * @param termVectors          {@code true} if term vectors should be kept    * @param classFieldName       name of the field used as the label for classification; this must be indexed with sorted doc values    * @param fieldNames           names of fields that need to be put in the new indexes or<code>null</code> if all should be used    * @throws IOException if any writing operation fails on any of the indexes    */
DECL|method|split
specifier|public
name|void
name|split
parameter_list|(
name|LeafReader
name|originalIndex
parameter_list|,
name|Directory
name|trainingIndex
parameter_list|,
name|Directory
name|testIndex
parameter_list|,
name|Directory
name|crossValidationIndex
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|termVectors
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create IWs for train / test / cv IDXs
name|IndexWriter
name|testWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|testIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|cvWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|crossValidationIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|trainingWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|trainingIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
comment|// get the exact no. of existing classes
name|SortedDocValues
name|classValues
init|=
name|originalIndex
operator|.
name|getSortedDocValues
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|classValues
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"the classFieldName \""
operator|+
name|classFieldName
operator|+
literal|"\" must index sorted doc values"
argument_list|)
throw|;
block|}
name|int
name|noOfClasses
init|=
name|classValues
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
try|try
block|{
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|originalIndex
argument_list|)
decl_stmt|;
name|GroupingSearch
name|gs
init|=
operator|new
name|GroupingSearch
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
name|gs
operator|.
name|setGroupSort
argument_list|(
name|Sort
operator|.
name|INDEXORDER
argument_list|)
expr_stmt|;
name|gs
operator|.
name|setSortWithinGroup
argument_list|(
name|Sort
operator|.
name|INDEXORDER
argument_list|)
expr_stmt|;
name|gs
operator|.
name|setAllGroups
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|gs
operator|.
name|setGroupDocsLimit
argument_list|(
name|originalIndex
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|TopGroups
argument_list|<
name|Object
argument_list|>
name|topGroups
init|=
name|gs
operator|.
name|search
argument_list|(
name|indexSearcher
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|0
argument_list|,
name|noOfClasses
argument_list|)
decl_stmt|;
comment|// set the type to be indexed, stored, with term vectors
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
if|if
condition|(
name|termVectors
condition|)
block|{
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|int
name|b
init|=
literal|0
decl_stmt|;
comment|// iterate over existing documents
for|for
control|(
name|GroupDocs
name|group
range|:
name|topGroups
operator|.
name|groups
control|)
block|{
name|int
name|totalHits
init|=
name|group
operator|.
name|totalHits
decl_stmt|;
name|double
name|testSize
init|=
name|totalHits
operator|*
name|testRatio
decl_stmt|;
name|int
name|tc
init|=
literal|0
decl_stmt|;
name|double
name|cvSize
init|=
name|totalHits
operator|*
name|crossValidationRatio
decl_stmt|;
name|int
name|cvc
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|group
operator|.
name|scoreDocs
control|)
block|{
comment|// create a new document for indexing
name|Document
name|doc
init|=
name|createNewDoc
argument_list|(
name|originalIndex
argument_list|,
name|ft
argument_list|,
name|scoreDoc
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
comment|// add it to one of the IDXs
if|if
condition|(
name|b
operator|%
literal|2
operator|==
literal|0
operator|&&
name|tc
operator|<
name|testSize
condition|)
block|{
name|testWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|tc
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cvc
operator|<
name|cvSize
condition|)
block|{
name|cvWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cvc
operator|++
expr_stmt|;
block|}
else|else
block|{
name|trainingWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|b
operator|++
expr_stmt|;
block|}
block|}
comment|// commit
name|testWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cvWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|trainingWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// merge
name|testWriter
operator|.
name|forceMerge
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|cvWriter
operator|.
name|forceMerge
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|trainingWriter
operator|.
name|forceMerge
argument_list|(
literal|3
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// close IWs
name|testWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|cvWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|trainingWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|originalIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createNewDoc
specifier|private
name|Document
name|createNewDoc
parameter_list|(
name|LeafReader
name|originalIndex
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|ScoreDoc
name|scoreDoc
parameter_list|,
name|String
index|[]
name|fieldNames
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|originalIndex
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNames
operator|!=
literal|null
operator|&&
name|fieldNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|IndexableField
name|field
init|=
name|document
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldName
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|document
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|readerValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

