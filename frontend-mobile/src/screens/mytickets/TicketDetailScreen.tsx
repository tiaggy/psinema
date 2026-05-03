import { View, Text, StyleSheet, Alert, TouchableOpacity, ScrollView } from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import { cancelOrder } from '../../api/orders';
import { useQueryClient } from '@tanstack/react-query';

export default function TicketDetailScreen({ route, navigation }: any) {
  const { ticket } = route.params;
  const qc = useQueryClient();

  const handleCancel = () => {
    Alert.alert('Cancel ticket', 'Are you sure you want to cancel this ticket?', [
      { text: 'No' },
      {
        text: 'Yes', style: 'destructive', onPress: async () => {
          try {
            await cancelOrder(ticket.order.id, [ticket.id]);
            qc.invalidateQueries({ queryKey: ['myTickets'] });
            navigation.goBack();
          } catch (e: any) {
            Alert.alert('Error', e.response?.data?.message ?? 'Cannot cancel');
          }
        }
      },
    ]);
  };

  const isActive = ticket.status === 'VALID';

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.movieTitle}>{ticket.order.screening.movie.title}</Text>
      <Text style={styles.meta}>{new Date(ticket.order.screening.startTime).toLocaleString()}</Text>
      <Text style={styles.meta}>{ticket.order.screening.hall.name}</Text>
      <Text style={styles.seatInfo}>Row {ticket.seat.rowNumber} · Seat {ticket.seat.seatNumber} · {ticket.type}</Text>

      <View style={styles.qrBox}>
        {isActive ? (
          <>
            <QRCode value={ticket.qrCodeData} size={220} />
            <Text style={styles.qrCaption}>Present this QR code at the entrance</Text>
          </>
        ) : (
          <View style={styles.invalidBadge}>
            <Text style={styles.invalidText}>{ticket.status}</Text>
          </View>
        )}
      </View>

      {isActive && (
        <TouchableOpacity style={styles.cancelBtn} onPress={handleCancel}>
          <Text style={styles.cancelBtnText}>Cancel This Ticket</Text>
        </TouchableOpacity>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flexGrow: 1, backgroundColor: '#f5f5f5', padding: 24, alignItems: 'center' },
  movieTitle: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', textAlign: 'center', marginBottom: 4 },
  meta: { fontSize: 13, color: '#888', marginBottom: 2 },
  seatInfo: { fontSize: 15, fontWeight: '600', color: '#e94560', marginVertical: 8 },
  qrBox: { backgroundColor: '#fff', borderRadius: 12, padding: 24, marginVertical: 24, elevation: 2, alignItems: 'center' },
  qrCaption: { fontSize: 12, color: '#aaa', marginTop: 12, textAlign: 'center' },
  invalidBadge: { backgroundColor: '#eee', padding: 24, borderRadius: 8 },
  invalidText: { fontSize: 18, color: '#999', fontWeight: '700' },
  cancelBtn: { borderWidth: 1, borderColor: '#e74c3c', borderRadius: 8, paddingHorizontal: 24, paddingVertical: 12 },
  cancelBtnText: { color: '#e74c3c', fontWeight: '600' },
});
