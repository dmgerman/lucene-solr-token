begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package

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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * An abstract test class that prepares nested test classes to run.  * A nested test class will assume it's executed under control of this  * class and be ignored otherwise.   *   *<p>The purpose of this is so that nested test suites don't run from  * IDEs like Eclipse (where they are automatically detected).  *   *<p>This class cannot extend {@link LuceneTestCase} because in case  * there's a nested {@link LuceneTestCase} afterclass hooks run twice and  * cause havoc (static fields).  */
end_comment

begin_class
DECL|class|WithNestedTests
specifier|public
specifier|abstract
class|class
name|WithNestedTests
block|{
DECL|field|runsAsNested
specifier|public
specifier|static
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
name|runsAsNested
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Boolean
name|initialValue
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
DECL|class|AbstractNestedTest
specifier|public
specifier|static
specifier|abstract
class|class
name|AbstractNestedTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|isRunningNested
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isRunningNested
specifier|protected
specifier|static
name|boolean
name|isRunningNested
parameter_list|()
block|{
return|return
name|runsAsNested
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|runsAsNested
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|field|suppressOutputStreams
specifier|private
name|boolean
name|suppressOutputStreams
decl_stmt|;
DECL|method|WithNestedTests
specifier|protected
name|WithNestedTests
parameter_list|(
name|boolean
name|suppressOutputStreams
parameter_list|)
block|{
name|this
operator|.
name|suppressOutputStreams
operator|=
name|suppressOutputStreams
expr_stmt|;
block|}
DECL|field|prevSysErr
specifier|protected
name|PrintStream
name|prevSysErr
decl_stmt|;
DECL|field|prevSysOut
specifier|protected
name|PrintStream
name|prevSysOut
decl_stmt|;
DECL|field|sysout
specifier|private
name|ByteArrayOutputStream
name|sysout
decl_stmt|;
DECL|field|syserr
specifier|private
name|ByteArrayOutputStream
name|syserr
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
specifier|final
name|void
name|before
parameter_list|()
block|{
if|if
condition|(
name|suppressOutputStreams
condition|)
block|{
name|prevSysOut
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|prevSysErr
operator|=
name|System
operator|.
name|err
expr_stmt|;
try|try
block|{
name|sysout
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|sysout
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|syserr
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|syserr
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|runsAsNested
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
specifier|final
name|void
name|after
parameter_list|()
block|{
name|runsAsNested
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|suppressOutputStreams
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|prevSysOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|prevSysErr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSysOut
specifier|protected
name|String
name|getSysOut
parameter_list|()
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|suppressOutputStreams
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|sysout
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getSysErr
specifier|protected
name|String
name|getSysErr
parameter_list|()
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|suppressOutputStreams
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|syserr
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

