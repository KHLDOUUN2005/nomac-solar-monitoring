import os
import sys

# Allow tests to `import main` without needing the package installed or
# pytest invoked from inside src/.
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "src"))