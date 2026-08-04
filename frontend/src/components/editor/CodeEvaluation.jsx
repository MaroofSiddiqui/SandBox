import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { ProctoringWrapper, handleMonacoPaste } from '../proctoring/ProctoringWrapper';

const LANGUAGE_CONFIG = {
  java: {
    id: 62,
    monacoName: "java",
    defaultCode: `import java.util.Scanner;

public class Main {
    public static int solveMeFirst(int a, int b) {
        return a + b;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = Integer.parseInt(scanner.nextLine().trim());
        int b = Integer.parseInt(scanner.nextLine().trim());
        System.out.println(solveMeFirst(a, b));
    }
}`
  },
  python: {
    id: 71,
    monacoName: "python",
    defaultCode: `def solveMeFirst(a, b):
    return a + b

a = int(input())
b = int(input())
print(solveMeFirst(a, b))`
  },
  cpp: {
    id: 54,
    monacoName: "cpp",
    defaultCode: `#include <iostream>
using namespace std;

int solveMeFirst(int a, int b) {
    return a + b;
}

int main() {
    int a, b;
    cin >> a >> b;
    cout << solveMeFirst(a, b) << endl;
    return 0;
}`
  },
  javascript: {
    id: 63,
    monacoName: "javascript",
    defaultCode: `function solveMeFirst(a, b) {
    return a + b;
}

const readline = require('readline');
const rl = readline.createInterface({ input: process.stdin, terminal: false });
let lines = [];
rl.on('line', (line) => lines.push(line));
rl.on('close', () => {
    const a = parseInt(lines[0]);
    const b = parseInt(lines[1]);
    console.log(solveMeFirst(a, b));
});`
  }
};

const CANDIDATE_ID = "CAND-001";
const EXAM_SESSION_ID = "EXAM-001";
// Abhi ke liye fixed rakha hai — jab question-selection screen banega,
// isko route param (useParams) se lena hoga
const QUESTION_ID = "QUESTION_001";

const API_BASE = "http://localhost:8083/api";

const CodeEvaluation = () => {
  const [questionData, setQuestionData] = useState(null);
  const [questionError, setQuestionError] = useState('');

  const [selectedLang, setSelectedLang] = useState('java');
  const [sourceCode, setSourceCode] = useState(LANGUAGE_CONFIG['java'].defaultCode);

  // Console ab structured rahega — text nahi, entries ka array
  const [consoleEntries, setConsoleEntries] = useState([]);
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // --- Question ko backend se fetch karo jab component load ho ---
  useEffect(() => {
    const fetchQuestion = async () => {
      try {
        const response = await fetch(`${API_BASE}/questions/${QUESTION_ID}`);
        if (!response.ok) {
          setQuestionError(`Question '${QUESTION_ID}' not found in database.`);
          return;
        }
        const data = await response.json();
        setQuestionData(data);
      } catch (error) {
        setQuestionError("Could not connect to backend to fetch question.");
      }
    };
    fetchQuestion();
  }, []);

  const handleLanguageChange = (e) => {
    const newLang = e.target.value;
    setSelectedLang(newLang);
    setSourceCode(LANGUAGE_CONFIG[newLang].defaultCode);
  };

  // --- 1. RUN CODE — sirf visible test cases, real console jaisa per-case result ---
  const handleRunCode = async () => {
    setIsRunning(true);
    setConsoleEntries([{ type: 'info', text: 'Running visible test cases... ⏳' }]);

    try {
      const response = await fetch(`${API_BASE}/evaluations/run`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          sourceCode,
          languageId: LANGUAGE_CONFIG[selectedLang].id,
          questionId: QUESTION_ID
        })
      });

      const resultText = await response.text();
      let parsed;
      try {
        parsed = JSON.parse(resultText);
      } catch {
        setConsoleEntries([{ type: 'error', text: resultText }]);
        return;
      }

      if (!response.ok) {
        setConsoleEntries([{ type: 'error', text: parsed.error || "Failed to run code." }]);
        return;
      }

      // parsed = array of { testCaseNumber, input, expectedOutput, actualOutput, passed, errorMessage }
      const entries = parsed.map((tc) => ({
        type: 'testcase',
        testCaseNumber: tc.testCaseNumber,
        passed: tc.passed,
        input: tc.input,
        expectedOutput: tc.expectedOutput,
        actualOutput: tc.actualOutput,
        errorMessage: tc.errorMessage
      }));

      setConsoleEntries(entries);

    } catch (error) {
      setConsoleEntries([{ type: 'error', text: "Error: Could not connect to backend server on port 8083." }]);
    } finally {
      setIsRunning(false);
    }
  };

  // --- 2. SUBMIT CODE — sab test cases (hidden included) + AI evaluation ---
  const handleSubmitCode = async () => {
    setIsSubmitting(true);
    setConsoleEntries([{ type: 'info', text: 'Evaluating submission (test cases + AI review)... ⏳' }]);

    const payload = {
      sourceCode,
      languageId: LANGUAGE_CONFIG[selectedLang].id,
      questionId: QUESTION_ID,
      studentId: CANDIDATE_ID
    };

    try {
      const response = await fetch(`${API_BASE}/evaluations/submit-and-save`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const resultText = await response.text();
      let parsed;
      try {
        parsed = JSON.parse(resultText);
      } catch {
        setConsoleEntries([{ type: 'error', text: resultText }]);
        return;
      }

      if (!response.ok) {
        setConsoleEntries([{ type: 'error', text: parsed.error || "Submission failed." }]);
        return;
      }

      setConsoleEntries([{ type: 'submission', result: parsed }]);

    } catch (error) {
      setConsoleEntries([{ type: 'error', text: "Error: Code submission failed. Check backend connection." }]);
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- Console render helper ---
  const renderConsole = () => {
    if (consoleEntries.length === 0) {
      return <span style={{ color: '#888' }}>Output will appear here after running the code...</span>;
    }

    return consoleEntries.map((entry, idx) => {
      if (entry.type === 'info') {
        return <div key={idx} style={{ color: '#facc15' }}>{entry.text}</div>;
      }
      if (entry.type === 'error') {
        return <div key={idx} style={{ color: '#f87171', whiteSpace: 'pre-wrap' }}>{entry.text}</div>;
      }
      if (entry.type === 'testcase') {
        return (
          <div key={idx} style={{ marginBottom: '10px', borderLeft: `3px solid ${entry.passed ? '#4ade80' : '#f87171'}`, paddingLeft: '10px' }}>
            <div style={{ color: entry.passed ? '#4ade80' : '#f87171', fontWeight: 'bold' }}>
              Test Case {entry.testCaseNumber}: {entry.passed ? 'PASSED ✅' : 'FAILED ❌'}
            </div>
            <div style={{ color: '#aaa', fontSize: '13px' }}>Input: {entry.input}</div>
            <div style={{ color: '#aaa', fontSize: '13px' }}>Expected: {entry.expectedOutput}</div>
            <div style={{ color: '#aaa', fontSize: '13px' }}>Got: {entry.actualOutput}</div>
            {entry.errorMessage && (
              <div style={{ color: '#f87171', fontSize: '13px', whiteSpace: 'pre-wrap' }}>{entry.errorMessage}</div>
            )}
          </div>
        );
      }
      if (entry.type === 'submission') {
  const r = entry.result;
  return (
    <div key={idx}>
      <div style={{ color: '#4ade80', fontWeight: 'bold', fontSize: '16px', marginBottom: '8px' }}>
        ✅ {r.message}
      </div>
      <div style={{ color: '#e2e8f0' }}>
        Hidden Test Cases Passed: {r.hiddenTestsPassed} / {r.hiddenTotalTests}
      </div>
    </div>
  );
}
      return null;
    });
  };

  if (questionError) {
    return (
      <div style={{ padding: '40px', color: '#dc2626', fontFamily: 'Arial, sans-serif' }}>
        {questionError}
      </div>
    );
  }

  if (!questionData) {
    return (
      <div style={{ padding: '40px', color: '#334155', fontFamily: 'Arial, sans-serif' }}>
        Loading question...
      </div>
    );
  }

  return (
    <ProctoringWrapper candidateId={CANDIDATE_ID} examSessionId={EXAM_SESSION_ID}>
      <div style={{ display: 'flex', height: '100vh', backgroundColor: '#f3f4f6', fontFamily: 'Arial, sans-serif' }}>

        {/* LEFT PANEL */}
        <div style={{ width: '40%', padding: '20px', borderRight: '1px solid #ccc', overflowY: 'auto', backgroundColor: '#ffffff' , userSelect: 'none'}}>
          <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>{questionData.title}</h2>
          <p style={{ color: '#334155', lineHeight: '1.6' }}>{questionData.description}</p>

          {questionData.constraints && (
            <>
              <h4 style={{ borderBottom: '1px solid #eee', paddingBottom: '5px' }}>Constraints</h4>
              <pre style={{ background: '#f8fafc', padding: '10px', borderRadius: '5px', color: '#dc2626' }}>{questionData.constraints}</pre>
            </>
          )}

          {/* Visible test cases yahi se dikha rahe hain — hidden wale kabhi frontend pe aate hi nahi */}
          <h4 style={{ borderBottom: '1px solid #eee', paddingBottom: '5px' }}>Sample Test Cases</h4>
          {(questionData.testCases || [])
            .filter((tc) => !tc.hidden)
            .map((tc, idx) => (
              <div key={idx} style={{ marginBottom: '10px' }}>
                <pre style={{ background: '#f8fafc', padding: '10px', borderRadius: '5px', marginBottom: '4px' }}>Input: {tc.input}</pre>
                <pre style={{ background: '#f8fafc', padding: '10px', borderRadius: '5px' }}>Expected Output: {tc.expectedOutput}</pre>
              </div>
            ))}
        </div>

        {/* RIGHT PANEL */}
        <div style={{ width: '60%', display: 'flex', flexDirection: 'column' }}>

          <div style={{ padding: '10px 20px', backgroundColor: '#1e1e1e', color: '#ccc', fontSize: '14px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <label htmlFor="language-select" style={{ marginRight: '10px' }}>Language:</label>
              <select
                id="language-select"
                value={selectedLang}
                onChange={handleLanguageChange}
                style={{ padding: '4px 8px', borderRadius: '4px', backgroundColor: '#333', color: '#fff', border: '1px solid #555' }}
              >
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
                <option value="javascript">JavaScript</option>
              </select>
            </div>
            <span>Theme: VS-Dark</span>
          </div>

          <div style={{ flexGrow: 1 }}>
            <Editor
              height="100%"
              language={LANGUAGE_CONFIG[selectedLang].monacoName}
              theme="vs-dark"
              value={sourceCode}
              onChange={(value) => setSourceCode(value)}
              options={{ fontSize: 16, minimap: { enabled: false } }}
              onMount={(editor) => {
                editor.onDidPaste(() => handleMonacoPaste(CANDIDATE_ID, EXAM_SESSION_ID));
              }}
            />
          </div>

          <div style={{ height: '220px', backgroundColor: '#1e1e1e', padding: '15px', borderTop: '2px solid #333', overflowY: 'auto', fontFamily: 'monospace', fontSize: '13px' }}>
            <div style={{ color: '#888', marginBottom: '8px', fontSize: '12px', textTransform: 'uppercase' }}>Console Output</div>
            {renderConsole()}
          </div>

          <div style={{ padding: '15px 20px', backgroundColor: '#ffffff', borderTop: '1px solid #ccc', display: 'flex', justifyContent: 'flex-end', gap: '15px' }}>
            <button
              onClick={handleRunCode}
              disabled={isRunning || isSubmitting}
              style={{ padding: '10px 24px', backgroundColor: (isRunning || isSubmitting) ? '#ccc' : '#e2e8f0', color: '#1e293b', border: 'none', borderRadius: '6px', cursor: (isRunning || isSubmitting) ? 'not-allowed' : 'pointer', fontWeight: 'bold' }}>
              {isRunning ? "Running..." : "Run Code"}
            </button>
            <button
              onClick={handleSubmitCode}
              disabled={isRunning || isSubmitting}
              style={{ padding: '10px 24px', backgroundColor: (isRunning || isSubmitting) ? '#ccc' : '#2563eb', color: '#ffffff', border: 'none', borderRadius: '6px', cursor: (isRunning || isSubmitting) ? 'not-allowed' : 'pointer', fontWeight: 'bold' }}>
              {isSubmitting ? "Submitting..." : "Submit Code"}
            </button>
          </div>

        </div>
      </div>
    </ProctoringWrapper>
  );
};

export default CodeEvaluation;