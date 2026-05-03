import { useEffect, useRef, useState } from 'react';
import { Html5Qrcode } from 'html5-qrcode';
import { validateTicket } from '../api/tickets';
import styles from './QrValidationPage.module.css';

type Result = { ok: boolean; message: string; detail?: string };

export default function QrValidationPage() {
  const [scanning, setScanning] = useState(false);
  const [result, setResult] = useState<Result | null>(null);
  const [manualCode, setManualCode] = useState('');
  const [mirrored, setMirrored] = useState(false);
  const scannerRef = useRef<Html5Qrcode | null>(null);

  const handleCode = async (code: string) => {
    try {
      const ticket = await validateTicket(code);
      setResult({
        ok: true,
        message: '✅ Valid ticket — entry allowed',
        detail: `Seat R${ticket.seat.rowNumber}/S${ticket.seat.seatNumber} · ${ticket.type}`,
      });
    } catch (e: any) {
      setResult({
        ok: false,
        message: '❌ Invalid ticket',
        detail: e.response?.data?.message ?? 'Unknown error',
      });
    }
  };

  const startScan = async () => {
    setResult(null);
    const qr = new Html5Qrcode('qr-reader');
    scannerRef.current = qr;
    setScanning(true);
    await qr.start(
      { facingMode: 'environment' },
      { fps: 10, qrbox: 250 },
      async (decoded) => {
        await qr.stop();
        setScanning(false);
        handleCode(decoded);
      },
      () => {}
    );
  };

  const stopScan = async () => {
    if (scannerRef.current) {
      await scannerRef.current.stop().catch(() => {});
      setScanning(false);
    }
  };

  useEffect(() => () => { stopScan(); }, []);

  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>Ticket Validation</h1>

      <div className={styles.scanner}>
        <div id="qr-reader" className={styles.qrReader} style={mirrored ? { transform: 'scaleX(-1)' } : undefined} />
        {!scanning ? (
          <button className={styles.btn} onClick={startScan}>Start Camera Scan</button>
        ) : (
          <div style={{ display: 'flex', gap: 10 }}>
            <button className={styles.btnSecondary} onClick={stopScan}>Stop Scanning</button>
            <button className={styles.btnSecondary} onClick={() => setMirrored(m => !m)}>
              {mirrored ? 'Unmirror' : 'Mirror'}
            </button>
          </div>
        )}
      </div>

      <div className={styles.divider}>— or enter code manually —</div>

      <div className={styles.manual}>
        <input
          className={styles.input}
          placeholder="Paste QR code data (UUID)"
          value={manualCode}
          onChange={e => setManualCode(e.target.value)}
        />
        <button className={styles.btn} onClick={() => { if (manualCode) handleCode(manualCode); }} disabled={!manualCode}>
          Validate
        </button>
      </div>

      {result && (
        <div className={`${styles.result} ${result.ok ? styles.ok : styles.fail}`}>
          <div className={styles.resultMsg}>{result.message}</div>
          {result.detail && <div className={styles.resultDetail}>{result.detail}</div>}
          <button className={styles.btnClear} onClick={() => { setResult(null); setManualCode(''); }}>Clear</button>
        </div>
      )}
    </div>
  );
}
