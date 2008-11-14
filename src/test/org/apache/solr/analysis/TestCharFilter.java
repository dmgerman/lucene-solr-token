begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestCharFilter
specifier|public
class|class
name|TestCharFilter
extends|extends
name|TestCase
block|{
DECL|method|testCharFilter1
specifier|public
name|void
name|testCharFilter1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter1
argument_list|(
operator|new
name|CharReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected position is invalid"
argument_list|,
literal|1
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter2
specifier|public
name|void
name|testCharFilter2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter2
argument_list|(
operator|new
name|CharReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected position is invalid"
argument_list|,
literal|2
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter12
specifier|public
name|void
name|testCharFilter12
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter2
argument_list|(
operator|new
name|CharFilter1
argument_list|(
operator|new
name|CharReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected position is invalid"
argument_list|,
literal|3
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter11
specifier|public
name|void
name|testCharFilter11
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter1
argument_list|(
operator|new
name|CharFilter1
argument_list|(
operator|new
name|CharReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected position is invalid"
argument_list|,
literal|2
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|CharFilter1
specifier|static
class|class
name|CharFilter1
extends|extends
name|CharFilter
block|{
DECL|method|CharFilter1
specifier|protected
name|CharFilter1
parameter_list|(
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|correctPosition
specifier|protected
name|int
name|correctPosition
parameter_list|(
name|int
name|currentPos
parameter_list|)
block|{
return|return
name|currentPos
operator|+
literal|1
return|;
block|}
block|}
DECL|class|CharFilter2
specifier|static
class|class
name|CharFilter2
extends|extends
name|CharFilter
block|{
DECL|method|CharFilter2
specifier|protected
name|CharFilter2
parameter_list|(
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|correctPosition
specifier|protected
name|int
name|correctPosition
parameter_list|(
name|int
name|currentPos
parameter_list|)
block|{
return|return
name|currentPos
operator|+
literal|2
return|;
block|}
block|}
block|}
end_class

end_unit

