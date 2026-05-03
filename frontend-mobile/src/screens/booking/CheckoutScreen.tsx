import { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Alert, ScrollView, ActivityIndicator } from 'react-native';
import { useCartStore } from '../../store/cartStore';
import { confirmOrder } from '../../api/orders';
import { placeSnackOrder } from '../../api/snacks';

export default function CheckoutScreen({ route, navigation }: any) {
  const { screening } = route.params;
  const { selectedSeatIds, reservationIds, snackItems, clearCart } = useCartStore();
  const [loading, setLoading] = useState(false);

  const ticketTotal = selectedSeatIds.length * (screening.basePrice ?? 0);
  const snackTotal = snackItems.reduce((sum, i) => sum + i.price * i.quantity, 0);
  const grandTotal = ticketTotal + snackTotal;

  const handleConfirm = async () => {
    setLoading(true);
    try {
      const order = await confirmOrder({
        screeningId: screening.id,
        reservationIds,
        paymentToken: 'mock-token',
      });
      if (snackItems.length > 0) {
        await placeSnackOrder(order.id, snackItems.map(i => ({ snackItemId: i.snackItemId, quantity: i.quantity })));
      }
      clearCart();
      Alert.alert('Booking confirmed! 🎉', 'Check your tickets in the My Tickets tab.', [
        { text: 'OK', onPress: () => navigation.reset({ index: 0, routes: [{ name: 'MainTabs' }] }) },
      ]);
    } catch (e: any) {
      Alert.alert('Error', e.response?.data?.message ?? 'Could not complete booking');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ padding: 16 }}>
      <Text style={styles.title}>Order Summary</Text>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>🎬 Screening</Text>
        <Text style={styles.detail}>{screening.movie.title}</Text>
        <Text style={styles.meta}>{new Date(screening.startTime).toLocaleString()} · {screening.hall.name}</Text>
        <Text style={styles.meta}>{selectedSeatIds.length} ticket(s) × €{screening.basePrice} = €{ticketTotal.toFixed(2)}</Text>
      </View>

      {snackItems.length > 0 && (
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>🍿 Snacks</Text>
          {snackItems.map(i => (
            <Text key={i.snackItemId} style={styles.meta}>{i.name} ×{i.quantity} = €{(i.price * i.quantity).toFixed(2)}</Text>
          ))}
          <Text style={styles.meta}>Subtotal: €{snackTotal.toFixed(2)}</Text>
        </View>
      )}

      <View style={styles.totalRow}>
        <Text style={styles.totalLabel}>Total</Text>
        <Text style={styles.totalAmt}>€{grandTotal.toFixed(2)}</Text>
      </View>

      <View style={styles.mockPayment}>
        <Text style={styles.mockPaymentText}>💳 Payment will be processed via demo gateway</Text>
      </View>

      <TouchableOpacity style={styles.btn} onPress={handleConfirm} disabled={loading}>
        {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.btnText}>Confirm & Pay</Text>}
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  title: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', marginBottom: 16 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 16, marginBottom: 12, elevation: 1 },
  sectionTitle: { fontSize: 14, fontWeight: '700', color: '#1a1a2e', marginBottom: 8 },
  detail: { fontSize: 15, color: '#1a1a2e', fontWeight: '600' },
  meta: { fontSize: 13, color: '#666', marginTop: 2 },
  totalRow: { flexDirection: 'row', justifyContent: 'space-between', padding: 16, backgroundColor: '#fff', borderRadius: 8, marginBottom: 12, elevation: 1 },
  totalLabel: { fontSize: 16, fontWeight: '600', color: '#1a1a2e' },
  totalAmt: { fontSize: 20, fontWeight: '700', color: '#e94560' },
  mockPayment: { backgroundColor: '#fff8e1', borderRadius: 8, padding: 12, marginBottom: 16 },
  mockPaymentText: { fontSize: 12, color: '#b7791f', textAlign: 'center' },
  btn: { backgroundColor: '#e94560', borderRadius: 8, padding: 16, alignItems: 'center' },
  btnText: { color: '#fff', fontSize: 16, fontWeight: '700' },
});
