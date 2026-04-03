import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import type { SeatAvailability } from '../api/screenings';

interface HallMapProps {
  seats: SeatAvailability[];
  selectedSeatIds: number[];
  onToggleSeat: (id: number) => void;
}

const STATUS_COLOR: Record<string, string> = {
  AVAILABLE: '#2ecc71',
  TEMPORARILY_RESERVED: '#f39c12',
  TAKEN: '#ccc',
};

export default function HallMap({ seats, selectedSeatIds, onToggleSeat }: HallMapProps) {
  const rows: Record<number, SeatAvailability[]> = {};
  seats.forEach(s => {
    if (!rows[s.rowNumber]) rows[s.rowNumber] = [];
    rows[s.rowNumber].push(s);
  });

  return (
    <ScrollView horizontal showsHorizontalScrollIndicator={false}>
      <ScrollView>
        <View style={styles.screen}><Text style={styles.screenText}>SCREEN</Text></View>
        {Object.entries(rows).map(([row, rowSeats]) => (
          <View key={row} style={styles.row}>
            <Text style={styles.rowLabel}>{row}</Text>
            {rowSeats.sort((a, b) => a.seatNumber - b.seatNumber).map(seat => {
              const isSelected = selectedSeatIds.includes(seat.seatId);
              const isTaken = seat.status === 'TAKEN';
              const bgColor = isSelected ? '#3498db' : STATUS_COLOR[seat.status];
              return (
                <TouchableOpacity
                  key={seat.seatId}
                  style={[styles.seat, { backgroundColor: bgColor }]}
                  onPress={() => !isTaken && onToggleSeat(seat.seatId)}
                  disabled={isTaken}
                >
                  <Text style={styles.seatText}>{seat.seatNumber}</Text>
                </TouchableOpacity>
              );
            })}
          </View>
        ))}
        <View style={styles.legend}>
          <LegendItem color="#2ecc71" label="Available" />
          <LegendItem color="#3498db" label="Selected" />
          <LegendItem color="#f39c12" label="Reserved" />
          <LegendItem color="#ccc" label="Taken" />
        </View>
      </ScrollView>
    </ScrollView>
  );
}

function LegendItem({ color, label }: { color: string; label: string }) {
  return (
    <View style={styles.legendItem}>
      <View style={[styles.legendDot, { backgroundColor: color }]} />
      <Text style={styles.legendText}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { backgroundColor: '#1a1a2e', borderRadius: 4, padding: 6, marginHorizontal: 8, marginBottom: 16, alignItems: 'center' },
  screenText: { color: '#aaa', fontSize: 11, letterSpacing: 4 },
  row: { flexDirection: 'row', alignItems: 'center', marginBottom: 6 },
  rowLabel: { width: 20, fontSize: 11, color: '#888', textAlign: 'center' },
  seat: { width: 28, height: 28, borderRadius: 5, margin: 2, alignItems: 'center', justifyContent: 'center' },
  seatText: { fontSize: 9, color: '#fff', fontWeight: '600' },
  legend: { flexDirection: 'row', flexWrap: 'wrap', gap: 12, marginTop: 16, paddingHorizontal: 8 },
  legendItem: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  legendDot: { width: 12, height: 12, borderRadius: 3 },
  legendText: { fontSize: 11, color: '#666' },
});
