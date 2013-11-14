begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|facet
operator|.
name|FacetTestCase
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
name|TaxonomyReader
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
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|util
operator|.
name|PrintTaxonomyStats
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
name|RandomIndexWriter
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
name|similarities
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
name|search
operator|.
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|similarities
operator|.
name|Similarity
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestTaxonomyFacets
specifier|public
class|class
name|TestTaxonomyFacets
extends|extends
name|FacetTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// Writes facet ords to a separate directory from the
comment|// main index:
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|fts
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|fts
operator|.
name|setHierarchical
argument_list|(
literal|"Publish Date"
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|FacetIndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|taxoWriter
argument_list|,
name|fts
argument_list|)
decl_stmt|;
comment|// Reused across documents, to add the necessary facet
comment|// fields:
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"2010"
argument_list|,
literal|"10"
argument_list|,
literal|"15"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"2010"
argument_list|,
literal|"10"
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"2012"
argument_list|,
literal|"1"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Susan"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"2012"
argument_list|,
literal|"1"
argument_list|,
literal|"7"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Frank"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"1999"
argument_list|,
literal|"5"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// NRT open
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Aggregate the facet counts:
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
comment|// MatchAllDocsQuery is for "browsing" (counts facets
comment|// for all non-deleted docs in the index); normally
comment|// you'd use a "normal" query, and use MultiCollector to
comment|// wrap collecting the "normal" hits and also facets:
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|TaxonomyFacetCounts
name|facets
init|=
operator|new
name|TaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|fts
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Retrieve& verify results:
name|assertEquals
argument_list|(
literal|"Publish Date (5)\n  2010 (2)\n  2012 (2)\n  1999 (1)\n"
argument_list|,
name|facets
operator|.
name|getDim
argument_list|(
literal|"Publish Date"
argument_list|,
literal|10
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Author (5)\n  Lisa (2)\n  Bob (1)\n  Susan (1)\n  Frank (1)\n"
argument_list|,
name|facets
operator|.
name|getDim
argument_list|(
literal|"Author"
argument_list|,
literal|10
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now user drills down on Publish Date/2010:
name|SimpleDrillDownQuery
name|q2
init|=
operator|new
name|SimpleDrillDownQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Date"
argument_list|,
literal|"2010"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|SimpleFacetsCollector
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|facets
operator|=
operator|new
name|TaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|fts
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Author (2)\n  Bob (1)\n  Lisa (1)\n"
argument_list|,
name|facets
operator|.
name|getDim
argument_list|(
literal|"Author"
argument_list|,
literal|10
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facets
operator|.
name|getSpecificCount
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Smoke test PrintTaxonomyStats:
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintTaxonomyStats
operator|.
name|printStats
argument_list|(
name|taxoReader
argument_list|,
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|,
literal|false
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|bos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"/Author: 4 immediate children; 5 total categories"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"/Publish Date: 3 immediate children; 12 total categories"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Make sure at least a few nodes of the tree came out:
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"  /1999"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"  /2012"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"      /20"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5333
DECL|method|testSparseFacets
specifier|public
name|void
name|testSparseFacets
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// Writes facet ords to a separate directory from the
comment|// main index:
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|FacetIndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|taxoWriter
argument_list|,
operator|new
name|FacetsConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo3"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"c"
argument_list|,
literal|"baz1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// NRT open
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|TaxonomyFacetCounts
name|facets
init|=
operator|new
name|TaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
operator|new
name|FacetsConfig
argument_list|()
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|List
argument_list|<
name|SimpleFacetResult
argument_list|>
name|results
init|=
name|facets
operator|.
name|getAllDims
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a (3)\n  foo1 (1)\n  foo2 (1)\n  foo3 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b (2)\n  bar1 (1)\n  bar2 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c (1)\n  baz1 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// nocommit in the sparse case test that we are really
comment|// sorting by the correct dim count
comment|/*   public void testReallyNoNormsForDrillDown() throws Exception {     Directory dir = newDirectory();     Directory taxoDir = newDirectory();     IndexWriterConfig iwc = newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random()));     iwc.setSimilarity(new PerFieldSimilarityWrapper() {         final Similarity sim = new DefaultSimilarity();          @Override         public Similarity get(String name) {           assertEquals("field", name);           return sim;         }       });     RandomIndexWriter writer = new RandomIndexWriter(random(), dir, iwc);     TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir, IndexWriterConfig.OpenMode.CREATE);     FacetFields facetFields = new FacetFields(taxoWriter);            Document doc = new Document();     doc.add(newTextField("field", "text", Field.Store.NO));     facetFields.addFields(doc, Collections.singletonList(new CategoryPath("a/path", '/')));     writer.addDocument(doc);     writer.close();     taxoWriter.close();     dir.close();     taxoDir.close();   }    public void testAllParents() throws Exception {     Directory dir = newDirectory();     Directory taxoDir = newDirectory();     RandomIndexWriter writer = new RandomIndexWriter(random(), dir);     DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir, IndexWriterConfig.OpenMode.CREATE);      CategoryListParams clp = new CategoryListParams("$facets") {         @Override         public OrdinalPolicy getOrdinalPolicy(String fieldName) {           return OrdinalPolicy.ALL_PARENTS;         }       };     FacetIndexingParams fip = new FacetIndexingParams(clp);      FacetFields facetFields = new FacetFields(taxoWriter, fip);      Document doc = new Document();     doc.add(newTextField("field", "text", Field.Store.NO));     facetFields.addFields(doc, Collections.singletonList(new CategoryPath("a/path", '/')));     writer.addDocument(doc);      // NRT open     IndexSearcher searcher = newSearcher(writer.getReader());     writer.close();      // NRT open     TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoWriter);     taxoWriter.close();          FacetSearchParams fsp = new FacetSearchParams(fip,                                                   new CountFacetRequest(new CategoryPath("a", '/'), 10));      // Aggregate the facet counts:     FacetsCollector c = FacetsCollector.create(fsp, searcher.getIndexReader(), taxoReader);      // MatchAllDocsQuery is for "browsing" (counts facets     // for all non-deleted docs in the index); normally     // you'd use a "normal" query, and use MultiCollector to     // wrap collecting the "normal" hits and also facets:     searcher.search(new MatchAllDocsQuery(), c);     List<FacetResult> results = c.getFacetResults();     assertEquals(1, results.size());     assertEquals(1, (int) results.get(0).getFacetResultNode().value);      // LUCENE-4913:     for(FacetResultNode childNode : results.get(0).getFacetResultNode().subResults) {       assertTrue(childNode.ordinal != 0);     }      searcher.getIndexReader().close();     taxoReader.close();     dir.close();     taxoDir.close();   }    public void testLabelWithDelimiter() throws Exception {     Directory dir = newDirectory();     Directory taxoDir = newDirectory();     RandomIndexWriter writer = new RandomIndexWriter(random(), dir);     DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir, IndexWriterConfig.OpenMode.CREATE);      FacetFields facetFields = new FacetFields(taxoWriter);      Document doc = new Document();     doc.add(newTextField("field", "text", Field.Store.NO));     BytesRef br = new BytesRef(new byte[] {(byte) 0xee, (byte) 0x92, (byte) 0xaa, (byte) 0xef, (byte) 0x9d, (byte) 0x89});     facetFields.addFields(doc, Collections.singletonList(new CategoryPath("dim/" + br.utf8ToString(), '/')));     try {       writer.addDocument(doc);     } catch (IllegalArgumentException iae) {       // expected     }     writer.close();     taxoWriter.close();     dir.close();     taxoDir.close();   }      // LUCENE-4583: make sure if we require> 32 KB for one   // document, we don't hit exc when using Facet42DocValuesFormat   public void testManyFacetsInOneDocument() throws Exception {     assumeTrue("default Codec doesn't support huge BinaryDocValues", _TestUtil.fieldSupportsHugeBinaryDocValues(CategoryListParams.DEFAULT_FIELD));     Directory dir = newDirectory();     Directory taxoDir = newDirectory();     IndexWriterConfig iwc = newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random()));     RandomIndexWriter writer = new RandomIndexWriter(random(), dir, iwc);     DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir, IndexWriterConfig.OpenMode.CREATE);          FacetFields facetFields = new FacetFields(taxoWriter);          int numLabels = _TestUtil.nextInt(random(), 40000, 100000);          Document doc = new Document();     doc.add(newTextField("field", "text", Field.Store.NO));     List<CategoryPath> paths = new ArrayList<CategoryPath>();     for (int i = 0; i< numLabels; i++) {       paths.add(new CategoryPath("dim", "" + i));     }     facetFields.addFields(doc, paths);     writer.addDocument(doc);          // NRT open     IndexSearcher searcher = newSearcher(writer.getReader());     writer.close();          // NRT open     TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoWriter);     taxoWriter.close();          FacetSearchParams fsp = new FacetSearchParams(new CountFacetRequest(new CategoryPath("dim"), Integer.MAX_VALUE));          // Aggregate the facet counts:     FacetsCollector c = FacetsCollector.create(fsp, searcher.getIndexReader(), taxoReader);          // MatchAllDocsQuery is for "browsing" (counts facets     // for all non-deleted docs in the index); normally     // you'd use a "normal" query, and use MultiCollector to     // wrap collecting the "normal" hits and also facets:     searcher.search(new MatchAllDocsQuery(), c);     List<FacetResult> results = c.getFacetResults();     assertEquals(1, results.size());     FacetResultNode root = results.get(0).getFacetResultNode();     assertEquals(numLabels, root.subResults.size());     Set<String> allLabels = new HashSet<String>();     for (FacetResultNode childNode : root.subResults) {       assertEquals(2, childNode.label.length);       allLabels.add(childNode.label.components[1]);       assertEquals(1, (int) childNode.value);     }     assertEquals(numLabels, allLabels.size());          IOUtils.close(searcher.getIndexReader(), taxoReader, dir, taxoDir);   }   */
block|}
end_class

end_unit

