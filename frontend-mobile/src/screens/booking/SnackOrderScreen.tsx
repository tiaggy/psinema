import { View, Text, FlatList, TouchableOpacity, StyleSheet, ActivityIndicator, Image } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getSnacks } from '../../api/snacks';
import { useCartStore } from '../../store/cartStore';

export default function SnackOrderScreen({ route, navigation }: any) {
  const { screening } = route.params;
  const { snackItems, addSnack, removeSnack } = useCartStore();
  const { data: snacks, isLoading } = useQuery({ queryKey: ['snacks'], queryFn: getSnacks });

  const getQty = (id: number) => snackItems.find(i => i.snackItemId === id)?.quantity ?? 0;
  const snackTotal = snackItems.reduce((sum, i) => sum + i.price * i.quantity, 0);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Add Snacks</Text>
      <Text style={styles.subtitle}>Optional — order snacks along with your tickets</Text>
      {isLoading ? <ActivityIndicator color="#e94560" style={{ marginTop: 32 }} /> : (
        <FlatList
          data={snacks}
          keyExtractor={s => String(s.id)}
          contentContainerStyle={{ paddingBottom: 120 }}
          renderItem={({ item: s }) => {
            const qty = getQty(s.id);
            return (
              <View style={styles.snackRow}>
                {s.imageUrl ? <Image source={{ uri: s.imageUrl }} style={styles.snackImg} /> : <View style={[styles.snackImg, styles.snackImgPlaceholder]}><Text style={{ fontSize: 20 }}>🍿</Text></View>}
                <View style={styles.snackInfo}>
                  <Text style={styles.snackName}>{s.name}</Text>
                  <Text style={styles.snackPrice}>€{s.price}</Text>
                </View>
                <View style={styles.qty}>
                  {qty > 0 && <TouchableOpacity style={styles.qtyBtn} onPress={() => removeSnack(s.id)}><Text style={styles.qtyBtnText}>−</Text></TouchableOpacity>}
                  {qty > 0 && <Text style={styles.qtyText}>{qty}</Text>}
                  <TouchableOpacity style={styles.qtyBtn} onPress={() => addSnack(s.id, s.name, s.price)}><Text style={styles.qtyBtnText}>+</Text></TouchableOpacity>
                </View>
              </View>
            );
          }}
        />
      )}
      <View style={styles.footer}>
        <View>
          <Text style={styles.footerLabel}>Snacks</Text>
          <Text style={styles.footerAmt}>€{snackTotal.toFixed(2)}</Text>
        </View>
        <TouchableOpacity style={styles.btn} onPress={() => navigation.navigate('Checkout', { screening })}>
          <Text style={styles.btnText}>Checkout →</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  title: { fontSize: 20, fontWeight: '700', color: '#1a1a2e', padding: 16, paddingBottom: 4 },
  subtitle: { fontSize: 12, color: '#888', paddingHorizontal: 16, marginBottom: 12 },
  snackRow: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', marginHorizontal: 12, marginBottom: 8, borderRadius: 8, padding: 12, elevation: 1 },
  snackImg: { width: 50, height: 50, borderRadius: 6, marginRight: 12 },
  snackImgPlaceholder: { backgroundColor: '#f0f0f0', alignItems: 'center', justifyContent: 'center' },
  snackInfo: { flex: 1 },
  snackName: { fontSize: 14, fontWeight: '600', color: '#1a1a2e' },
  snackPrice: { fontSize: 13, color: '#e94560', marginTop: 2 },
  qty: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  qtyBtn: { width: 28, height: 28, borderRadius: 14, backgroundColor: '#e94560', alignItems: 'center', justifyContent: 'center' },
  qtyBtnText: { color: '#fff', fontSize: 16, fontWeight: '700' },
  qtyText: { fontSize: 15, fontWeight: '600', minWidth: 20, textAlign: 'center' },
  footer: { position: 'absolute', bottom: 0, left: 0, right: 0, backgroundColor: '#fff', padding: 16, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', elevation: 8 },
  footerLabel: { fontSize: 11, color: '#888' },
  footerAmt: { fontSize: 16, fontWeight: '700', color: '#1a1a2e' },
  btn: { backgroundColor: '#e94560', borderRadius: 8, paddingHorizontal: 20, paddingVertical: 12 },
  btnText: { color: '#fff', fontWeight: '600', fontSize: 15 },
});
