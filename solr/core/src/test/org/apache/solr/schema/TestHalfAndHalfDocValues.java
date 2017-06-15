begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|index
operator|.
name|DirectoryReader
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
name|DocValuesType
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
name|FieldInfos
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
name|LeafReaderContext
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
name|MultiFields
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
name|SolrTestCaseJ4
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
name|index
operator|.
name|NoMergePolicyFactory
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Added in SOLR-10047  */
end_comment

begin_class
DECL|class|TestHalfAndHalfDocValues
specifier|public
class|class
name|TestHalfAndHalfDocValues
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we need consistent segments that aren't merged because we want to have
comment|// segments with and without docvalues
name|systemSetPropertySolrTestsMergePolicyFactory
argument_list|(
name|NoMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-docValues.xml"
argument_list|)
expr_stmt|;
comment|// sanity check our schema meets our expectations
specifier|final
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
operator|new
name|String
index|[]
block|{
literal|"floatdv"
block|,
literal|"intdv"
block|,
literal|"doubledv"
block|,
literal|"longdv"
block|,
literal|"datedv"
block|,
literal|"stringdv"
block|,
literal|"booldv"
block|}
control|)
block|{
specifier|final
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|f
operator|+
literal|" is multiValued, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|multiValued
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|+
literal|" is indexed, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|indexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|+
literal|" has no docValues, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|hasDocValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHalfAndHalfDocValues
specifier|public
name|void
name|testHalfAndHalfDocValues
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Insert two docs without docvalues
name|String
name|fieldname
init|=
literal|"string_add_dv_later"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|fieldname
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldname
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCoreInc
argument_list|()
init|)
block|{
name|assertFalse
argument_list|(
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldname
argument_list|)
operator|.
name|hasDocValues
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add docvalues to the field type
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|oldField
init|=
name|schema
operator|.
name|getField
argument_list|(
name|fieldname
argument_list|)
decl_stmt|;
name|int
name|newProperties
init|=
name|oldField
operator|.
name|getProperties
argument_list|()
operator||
name|SchemaField
operator|.
name|DOC_VALUES
decl_stmt|;
name|SchemaField
name|sf
init|=
operator|new
name|SchemaField
argument_list|(
name|fieldname
argument_list|,
name|oldField
operator|.
name|getType
argument_list|()
argument_list|,
name|newProperties
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|fieldname
argument_list|,
name|sf
argument_list|)
expr_stmt|;
comment|// Insert a new doc with docvalues
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|fieldname
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check there are a mix of segments with and without docvalues
specifier|final
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherRef
init|=
name|core
operator|.
name|openNewSearcher
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SolrIndexSearcher
name|searcher
init|=
name|searcherRef
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|DirectoryReader
name|topReader
init|=
name|searcher
operator|.
name|getRawReader
argument_list|()
decl_stmt|;
comment|//Assert no merges
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|topReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|topReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfos
name|infos
init|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|topReader
argument_list|)
decl_stmt|;
comment|//The global field type should have docValues because a document with dvs was added
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|,
name|infos
operator|.
name|fieldInfo
argument_list|(
name|fieldname
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|topReader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|//Make sure there were no merges
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|NONE
argument_list|,
name|r
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|fieldname
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|,
name|r
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|fieldname
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|searcherRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Assert sort order is correct
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"string_add_dv_later:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"string_add_dv_later asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=3]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

